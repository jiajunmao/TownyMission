package world.naturecraft.townymission.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.bukkit.api.exceptions.DbConnectException;
import world.naturecraft.townymission.bukkit.commands.*;
import world.naturecraft.townymission.bukkit.commands.admin.TownyMissionAdminListMissions;
import world.naturecraft.townymission.bukkit.commands.admin.TownyMissionAdminReload;
import world.naturecraft.townymission.bukkit.commands.admin.TownyMissionAdminRoot;
import world.naturecraft.townymission.bukkit.commands.admin.TownyMissionAdminStartSeason;
import world.naturecraft.townymission.bukkit.listeners.external.MissionListener;
import world.naturecraft.townymission.bukkit.listeners.external.TownFallListener;
import world.naturecraft.townymission.bukkit.listeners.internal.DoMissionListener;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.components.enums.ServerType;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.bukkit.gui.MissionManageGui;
import world.naturecraft.townymission.core.config.LangConfig;
import world.naturecraft.townymission.core.config.MainConfig;
import world.naturecraft.townymission.core.config.StatsConfig;
import world.naturecraft.townymission.core.config.mission.MissionConfig;
import world.naturecraft.townymission.core.services.ChatService;
import world.naturecraft.townymission.core.services.StorageService;
import world.naturecraft.townymission.core.services.TimerService;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * The type Towny mission.
 */
public class TownyMissionBukkit extends JavaPlugin implements TownyMissionInstance {

    private final Logger logger = this.getLogger();
    private MissionConfig missionConfig;
    private StatsConfig statsConfig;
    private MainConfig mainConfig;
    private LangConfig langConfig;
    private TownyMissionRoot rootCmd;
    private StorageType storageType;

    @Override
    public void onEnable() {

        logger.info(ChatService.getInstance().translateColor("{#22DDBA}" + "  _______                        __  __ _         _             "));
        logger.info(ChatService.getInstance().translateColor("{#22DDBA}" + " |__   __|                      |  \\/  (_)       (_)            "));
        logger.info(ChatService.getInstance().translateColor("{#22DDBA}" + "    | | _____      ___ __  _   _| \\  / |_ ___ ___ _  ___  _ __  "));
        logger.info(ChatService.getInstance().translateColor("{#22DDBA}" + "    | |/ _ \\ \\ /\\ / / '_ \\| | | | |\\/| | / __/ __| |/ _ \\| '_ \\ "));
        logger.info(ChatService.getInstance().translateColor("{#22DDBA}" + "    | | (_) \\ V  V /| | | | |_| | |  | | \\__ \\__ \\ | (_) | | | |"));
        logger.info(ChatService.getInstance().translateColor("{#22DDBA}" + "    |_|\\___/ \\_/\\_/ |_| |_|\\__, |_|  |_|_|___/___/_|\\___/|_| |_|"));
        logger.info(ChatService.getInstance().translateColor("{#22DDBA}" + "                            __/ |                               "));
        logger.info(ChatService.getInstance().translateColor("{#22DDBA}" + "                           |___/                                "));
        logger.info("-----------------------------------------------------------------");

        logger.info(ChatService.getInstance().translateColor("{#E9B728}===> Parsing configuration"));

        TownyMissionInstanceType.serverType = ServerType.BUKKIT;

        /**
         * This is saving the config.yml (Default config)
         */
        this.saveDefaultConfig();
        try {
            missionConfig = new MissionConfig();
            statsConfig = new StatsConfig();
            mainConfig = new MainConfig();
            langConfig = new LangConfig();
        } catch (ConfigLoadingException e) {
            logger.severe("IO operation fault during custom config initialization");
            e.printStackTrace();
            onDisable();
        }

        /**
         * Configure data storage, yaml, or mysql
         */
        logger.info(ChatService.getInstance().translateColor("{#E9B728}===> Connecting to datastore"));
        String storage = getConfig().getString("storage");
        storageType = StorageType.valueOf(storage.toUpperCase(Locale.ROOT));

        if (storageType.equals(StorageType.MYSQL)) {
            logger.info("Using MYSQL as storage backend");
            connect();
        } else {
            logger.info("Using YAML flat file as storage backend");
        }

        logger.info(ChatService.getInstance().translateColor("{#E9B728}===> Registering commands"));
        registerCommands();
        logger.info(ChatService.getInstance().translateColor("{#E9B728}===> Registering listeners"));
        registerListeners();
        logger.info(ChatService.getInstance().translateColor("{#E9B728}===> Registering timers"));
        registerTimers();
    }

    @Override
    public void onDisable() {
        logger.info("=========   TOWNYMISSION DISABLING   =========");
        if (storageType.equals(StorageType.MYSQL)) {
            if (StorageService.getInstance().getDataSource() != null) {
                StorageService.getInstance().closeDb();
            }
        }
        Bukkit.getPluginManager().disablePlugin(this);
    }

    /**
     * Register commands.
     */
    public void registerCommands() {
        this.rootCmd = new TownyMissionRoot(this);
        this.getCommand("townymission").setExecutor(rootCmd);

        // User commands
        rootCmd.registerCommand("list", new TownyMissionList(this));
        rootCmd.registerCommand("start", new TownyMissionStart(this));
        rootCmd.registerCommand("abort", new TownyMissionAbort(this));
        rootCmd.registerCommand("deposit", new TownyMissionDeposit(this));
        rootCmd.registerCommand("claim", new TownyMissionClaim(this));
        rootCmd.registerCommand("info", new TownyMissionInfo(this));
        rootCmd.registerCommand("rank", new TownyMissionRank(this));

        // Admin commands
        TownyMissionAdminRoot rootAdminCmd = new TownyMissionAdminRoot(this);
        rootCmd.registerCommand("admin", rootAdminCmd);
        rootAdminCmd.registerAdminCommand("listMissions", new TownyMissionAdminListMissions(this));
        rootAdminCmd.registerAdminCommand("reload", new TownyMissionAdminReload(this));
        rootAdminCmd.registerAdminCommand("startSeason", new TownyMissionAdminStartSeason(this));
    }

    /**
     * Register listeners.
     */
    public void registerListeners() {
        // Event listeners
        getServer().getPluginManager().registerEvents(new MissionListener(this), this);
        getServer().getPluginManager().registerEvents(new TownFallListener(this), this);
        getServer().getPluginManager().registerEvents(new DoMissionListener(this), this);

        // GUI listeners
        getServer().getPluginManager().registerEvents(new MissionManageGui(this), this);
    }

    /**
     * Register pmc.
     */
    public void registerPMC() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "townymission:main");

    }

    /**
     * Register timers.
     */
    public void registerTimers() {
        TimerService timerService = TimerService.getInstance();
        logger.info("Started sprint timer");
        timerService.startSprintTimer();
        logger.info("Started season timer");
        timerService.startSeasonTimer();
    }

    /**
     * Connect.
     */
    public void connect() {
        try {
            StorageService.getInstance().connectDb();
        } catch (DbConnectException e) {
            e.printStackTrace();
            onDisable();
        }
    }

    /**
     * Reload configs.
     *
     * @throws ConfigLoadingException the config loading exception
     */
    public void reloadConfigs() throws ConfigLoadingException {
        this.reloadConfig();
        missionConfig = new MissionConfig();
        statsConfig = new StatsConfig();
    }

    /**
     * Gets lang entry.
     *
     * @param path the path
     * @return the lang entry
     */
    @Override
    public String getLangEntry(String path) {
        String finalString = "";
        finalString += langConfig.getLangConfig().getString("prefix") + " ";
        finalString += langConfig.getLangConfig().getString(path);
        return finalString;
    }

    /**
     * Gets custom config.
     *
     * @return the custom config
     */
    public MissionConfig getCustomConfig() {
        return missionConfig;
    }

    @Override
    public StatsConfig getStatsConfig() {
        return statsConfig;
    }

    @Override
    public MainConfig getInstanceConfig() {
        return mainConfig;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUKKIT;
    }

    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    @Override
    public File getInstanceDataFolder() {
        return this.getDataFolder();
    }

    @Override
    public void saveInstanceResource(String filePath, boolean replace) {
        this.saveResource(filePath, replace);
    }

    @Override
    public InputStream getInstanceResource(String filePath) {
        return this.getResource(filePath);
    }
}
