package world.naturecraft.townymission.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigParsingException;
import world.naturecraft.townymission.bukkit.api.exceptions.DbConnectException;
import world.naturecraft.townymission.bukkit.commands.*;
import world.naturecraft.townymission.bukkit.commands.admin.*;
import world.naturecraft.townymission.bukkit.config.BukkitConfig;
import world.naturecraft.townymission.bukkit.config.reward.RewardConfigValidator;
import world.naturecraft.townymission.bukkit.gui.MissionManageGui;
import world.naturecraft.townymission.bukkit.listeners.external.TownFallListener;
import world.naturecraft.townymission.bukkit.listeners.external.mission.ExpansionListener;
import world.naturecraft.townymission.bukkit.listeners.external.mission.MobListener;
import world.naturecraft.townymission.bukkit.listeners.external.mission.MoneyListener;
import world.naturecraft.townymission.bukkit.listeners.external.mission.VoteListener;
import world.naturecraft.townymission.bukkit.listeners.internal.DoMissionListener;
import world.naturecraft.townymission.bukkit.listeners.internal.PMCListener;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.enums.ServerType;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.bukkit.config.mission.MissionConfig;
import world.naturecraft.townymission.core.config.TownyMissionConfig;
import world.naturecraft.townymission.core.services.ChatService;
import world.naturecraft.townymission.core.services.StorageService;
import world.naturecraft.townymission.core.services.TimerService;
import world.naturecraft.townymission.core.tasks.SendCachedMissionTask;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * The type Towny mission.
 */
public class TownyMissionBukkit extends JavaPlugin implements TownyMissionInstance {

    private final Logger logger = this.getLogger();

    private MissionConfig missionConfig;
    private TownyMissionConfig statsConfig;
    private TownyMissionConfig mainConfig;
    private TownyMissionConfig langConfig;
    private TownyMissionConfig doMissionCache;

    private TownyMissionRoot rootCmd;

    private StorageType storageType;
    private boolean isMainServer;
    private boolean isBungeecordEnabled;

    private HashMap<MissionType, Boolean> missionEnabled;

    @Override
    public void onEnable() {

        logger.info(BukkitUtil.translateColor("{#22DDBA}" + "  _______                        __  __ _         _             "));
        logger.info(BukkitUtil.translateColor("{#22DDBA}" + " |__   __|                      |  \\/  (_)       (_)            "));
        logger.info(BukkitUtil.translateColor("{#22DDBA}" + "    | | _____      ___ __  _   _| \\  / |_ ___ ___ _  ___  _ __  "));
        logger.info(BukkitUtil.translateColor("{#22DDBA}" + "    | |/ _ \\ \\ /\\ / / '_ \\| | | | |\\/| | / __/ __| |/ _ \\| '_ \\ "));
        logger.info(BukkitUtil.translateColor("{#22DDBA}" + "    | | (_) \\ V  V /| | | | |_| | |  | | \\__ \\__ \\ | (_) | | | |"));
        logger.info(BukkitUtil.translateColor("{#22DDBA}" + "    |_|\\___/ \\_/\\_/ |_| |_|\\__, |_|  |_|_|___/___/_|\\___/|_| |_|"));
        logger.info(BukkitUtil.translateColor("{#22DDBA}" + "                            __/ |                               "));
        logger.info(BukkitUtil.translateColor("{#22DDBA}" + "                           |___/                                "));
        logger.info("-----------------------------------------------------------------");

        TownyMissionInstanceType.serverType = ServerType.BUKKIT;
        missionEnabled = new HashMap<>();

        logger.info(BukkitUtil.translateColor("{#E9B728}===> Parsing main and lang config"));
        // Main config and lang config needs to be saved regardless
        mainConfig = new BukkitConfig("config.yml");
        langConfig = new BukkitConfig("lang.yml");
        langConfig.updateConfig("lang.yml");

        /**
         * Determine bungeecord is enabled, and whether this server is the main server
         */
        isMainServer = false;
        if (getConfig().getBoolean("bungeecord.enable")) {
            logger.info(BukkitUtil.translateColor("{#E9B728}===> Running BUNGEECORD mode"));
            logger.info(BukkitUtil.translateColor("{#E9B728}===> Registering Bungee Plugin Messaging Channel"));
            // This means that this bukkit instance should respond to the events
            registerPMC();
            isBungeecordEnabled = true;

            isMainServer = getConfig().getBoolean("bungeecord.main-server");
        }

        /**
         * Determine whether the server admin configured the hooks correctly
         */
        Collection<String> keys = mainConfig.getKeys("hooks.money");
        boolean enabled = false;
        for (String econHook : keys) {
            String fullPath = "hooks.money." + econHook;
            enabled = enabled || mainConfig.getBoolean(fullPath);
        }
        missionEnabled.put(MissionType.MONEY, enabled);
        if (!enabled) {
            logger.warning(BukkitUtil.translateColor("{#FF5241}  You have not enabled any econ hook, disabling money mission type."));
        }

        enabled = false;
        keys = mainConfig.getKeys("hooks.vote");
        for (String voteHook : keys) {
            String fullPath = "hooks.vote." + voteHook;
            enabled = enabled || mainConfig.getBoolean(fullPath);
        }
        missionEnabled.put(MissionType.VOTE, enabled);
        if (!enabled) {
            logger.warning(BukkitUtil.translateColor("{#FF5241}  You have not enabled any vote hook, disabling money mission type."));
        }

        /**
         * This is saving the config.yml (Default config)
         */
        logger.info(BukkitUtil.translateColor("{#E9B728}===> Parsing mission and rewards config"));
        try {
            // If bungeecord and not main server, don't save
            if (!isBungeecordEnabled || isMainServer) {
                logger.info(BukkitUtil.translateColor("{#E9B728}===> Parsing mission&stats config"));
                missionConfig = new MissionConfig();
                statsConfig = new BukkitConfig("datastore/stats.yml");
            } else {
                // This means that it is not the main server
                doMissionCache = new BukkitConfig("datastore/missionCache.yml");
            }

            RewardConfigValidator.checkRewardConfig();
        } catch (ConfigLoadingException e) {
            logger.severe("IO operation fault during custom config initialization");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        } catch (ConfigParsingException e) {
            logger.severe("There are errors in the reward section in config.yml! Please correct them and then reload plugin!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        /**
         * Configure data storage, yaml, or mysql
         */
        // If is bungee, and not main, do nothing here except for the listeners
        if (!isBungeecordEnabled || isMainServer) {
            logger.info(BukkitUtil.translateColor("{#E9B728}===> Connecting to datastore"));
            String storage = getConfig().getString("storage");
            storageType = StorageType.valueOf(storage.toUpperCase(Locale.ROOT));

            if (storageType.equals(StorageType.MYSQL)) {
                logger.info("Using MYSQL as storage backend");
                connect();
            } else {
                logger.info("Using YAML flat file as storage backend");
            }

            logger.info(BukkitUtil.translateColor("{#E9B728}===> Registering commands"));
            registerCommands();
            logger.info(BukkitUtil.translateColor("{#E9B728}===> Registering listeners"));
            registerListeners();
            logger.info(BukkitUtil.translateColor("{#E9B728}===> Registering timers"));
            registerTimers();
            logger.info(BukkitUtil.translateColor("{#E9B728}===> Registering tasks"));
            registerTasks();
        } else {
            registerListeners();
        }
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
    private void registerCommands() {
        if (!isBungeecordEnabled || isMainServer) {
            this.rootCmd = new TownyMissionRoot(this);
            this.getCommand("townymission").setExecutor(rootCmd);

            // User commands
            rootCmd.registerCommand("list", new TownyMissionList(this));
            rootCmd.registerCommand("abort", new TownyMissionAbort(this));
            rootCmd.registerCommand("deposit", new TownyMissionDeposit(this));
            rootCmd.registerCommand("claim", new TownyMissionClaim(this));
            rootCmd.registerCommand("info", new TownyMissionInfo(this));
            rootCmd.registerCommand("rank", new TownyMissionRank(this));
            rootCmd.registerCommand("reward", new TownyMissionReward(this));

            // Admin commands
            TownyMissionAdminRoot rootAdminCmd = new TownyMissionAdminRoot(this);
            rootCmd.registerCommand("admin", rootAdminCmd);
            rootAdminCmd.registerAdminCommand("listMissions", new TownyMissionAdminListMissions(this));
            rootAdminCmd.registerAdminCommand("reload", new TownyMissionAdminReload(this));
            rootAdminCmd.registerAdminCommand("startSeason", new TownyMissionAdminStartSeason(this));
            rootAdminCmd.registerAdminCommand("pauseSeason", new TownyMissionAdminPauseSeason(this));
        } else {
            this.rootCmd = new TownyMissionRoot(this);
            this.getCommand("townymission").setExecutor(rootCmd);

            rootCmd.registerCommand("list", new TownyMissionNonMain(this));
            rootCmd.registerCommand("abort", new TownyMissionNonMain(this));
            rootCmd.registerCommand("deposit", new TownyMissionDeposit(this));
            rootCmd.registerCommand("claim", new TownyMissionNonMain(this));
            rootCmd.registerCommand("info", new TownyMissionNonMain(this));
            rootCmd.registerCommand("rank", new TownyMissionNonMain(this));
            rootCmd.registerCommand("admin", new TownyMissionNonMain(this));
            rootCmd.registerCommand("reward", new TownyMissionNonMain(this));
        }
    }

    /**
     * Register listeners.
     */
    private void registerListeners() {
        // Event listeners
        // If bungee and !main, do not register town related listeners
        if (!isBungeecordEnabled || isMainServer) {
            getServer().getPluginManager().registerEvents(new ExpansionListener(this), this);

            getServer().getPluginManager().registerEvents(new TownFallListener(this), this);
            getServer().getPluginManager().registerEvents(new DoMissionListener(this), this);

            // GUI listeners
            getServer().getPluginManager().registerEvents(new MissionManageGui(this), this);
        }
        getServer().getPluginManager().registerEvents(new MobListener(this), this);
        getServer().getPluginManager().registerEvents(new MoneyListener(this), this);
        getServer().getPluginManager().registerEvents(new VoteListener(this), this);
    }

    /**
     * Register pmc.
     */
    private void registerPMC() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "townymission:main");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "townymission:main", new PMCListener());
    }

    /**
     * Register timers.
     */
    private void registerTimers() {
        TimerService timerService = TimerService.getInstance();
        logger.info("Started sprint timer");
        timerService.startSprintTimer();
        logger.info("Started season timer");
        timerService.startSeasonTimer();
    }

    private void registerTasks() {
        SendCachedMissionTask.registerTask();
        logger.info("Started send cache task");
    }

    /**
     * Connect.
     */
    private void connect() {
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
        mainConfig = new BukkitConfig("config.yml");
        this.reloadConfig();
        langConfig = new BukkitConfig("lang.yml");
        missionConfig = new MissionConfig();
        statsConfig = new BukkitConfig("datastore/stats.yml");
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
        finalString += langConfig.getString("prefix") + " ";
        finalString += langConfig.getString(path);
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
    public TownyMissionConfig getStatsConfig() {
        return statsConfig;
    }

    @Override
    public TownyMissionConfig getMissionCache() {
        return doMissionCache;
    }

    @Override
    public TownyMissionConfig getInstanceConfig() {
        return mainConfig;
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

    /**
     * Gets logger.
     *
     * @return the logger
     */
    @Override
    public Logger getInstanceLogger() {
        return this.getLogger();
    }

    public boolean isMainserver() {
        return !isBungeecordEnabled || isMainServer;
    }

    public boolean isMissionEnabled(MissionType missionType) {
        return missionEnabled.getOrDefault(missionType, true);
    }
}
