package world.naturecraft.townymission;

import com.Zrips.CMI.Modules.Placeholders.Placeholder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import world.naturecraft.townymission.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.api.exceptions.ConfigParsingException;
import world.naturecraft.townymission.api.exceptions.DbConnectException;
import world.naturecraft.townymission.commands.*;
import world.naturecraft.townymission.commands.admin.*;
import world.naturecraft.townymission.components.enums.*;
import world.naturecraft.townymission.config.BukkitConfig;
import world.naturecraft.townymission.config.TownyMissionConfig;
import world.naturecraft.townymission.config.mission.MissionConfig;
import world.naturecraft.townymission.config.reward.RewardConfigValidator;
import world.naturecraft.townymission.gui.MissionManageGui;
import world.naturecraft.townymission.listeners.DoMissionListener;
import world.naturecraft.townymission.listeners.PMCListener;
import world.naturecraft.townymission.listeners.TownFallListener;
import world.naturecraft.townymission.listeners.mission.ExpansionListener;
import world.naturecraft.townymission.listeners.mission.MobListener;
import world.naturecraft.townymission.listeners.mission.VoteListener;
import world.naturecraft.townymission.listeners.mission.money.CMIMoneyListener;
import world.naturecraft.townymission.listeners.mission.money.EssentialMoneyListener;
import world.naturecraft.townymission.services.PlaceholderBukkitService;
import world.naturecraft.townymission.services.StorageService;
import world.naturecraft.townymission.services.TimerService;
import world.naturecraft.townymission.tasks.SendCachedMissionTask;
import world.naturecraft.townymission.utils.BukkitUtil;

import java.io.File;
import java.io.InputStream;
import java.util.*;
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

    private TownyMissionRoot rootCmd;

    private StorageType storageType;
    private boolean isMainServer;
    private boolean isBungeecordEnabled;

    private HashMap<MissionType, List<String>> missionAndHooks;

    @Override
    public void onEnable() {

        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&3" + "  _______                        __  __ _         _             "));
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&3" + " |__   __|                      |  \\/  (_)       (_)            "));
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&3" + "    | | _____      ___ __  _   _| \\  / |_ ___ ___ _  ___  _ __  "));
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&3" + "    | |/ _ \\ \\ /\\ / / '_ \\| | | | |\\/| | / __/ __| |/ _ \\| '_ \\ "));
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&3" + "    | | (_) \\ V  V /| | | | |_| | |  | | \\__ \\__ \\ | (_) | | | |"));
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&3" + "    |_|\\___/ \\_/\\_/ |_| |_|\\__, |_|  |_|_|___/___/_|\\___/|_| |_|"));
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&3" + "                            __/ |                               "));
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&3" + "                           |___/                                "));
        getServer().getConsoleSender().sendMessage("-----------------------------------------------------------------");

        TownyMissionInstanceType.serverType = ServerType.BUKKIT;
        missionAndHooks = new HashMap<>();

        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Parsing main and lang config"));
        // Main configs
        mainConfig = new BukkitConfig("config.yml");
        mainConfig.updateConfig();

        // Saving new language file if there isn't one already
        for (LangType lang : LangType.values()) {
            String targetPath = "lang/" + lang.name() + ".yml";
            File targetFile = new File(getInstanceDataFolder(), targetPath);
            if (!targetFile.exists()) {
                targetFile.getParentFile().getParentFile().mkdirs();
                targetFile.getParentFile().mkdirs();
                saveInstanceResource(targetPath, false);
            }
        }

        String langFile = "lang/" + mainConfig.getString("language") + ".yml";
        System.out.println("Loading language file " + langFile);
        langConfig = new BukkitConfig(langFile);
        langConfig.updateConfig();

        determineBungeeCord();

        determineMissionEnabled();

        additionalConfigs();

        /**
         * Configure data storage, yaml, or mysql
         */
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Connecting to datastore"));
        String storage = getInstanceConfig().getString("storage");
        storageType = StorageType.valueOf(storage.toUpperCase(Locale.ROOT));

        if (storageType.equals(StorageType.MYSQL)) {
            getServer().getConsoleSender().sendMessage("Using MYSQL as storage backend");
            connect();
        } else {
            getServer().getConsoleSender().sendMessage("Using YAML flat file as storage backend");
        }

        /**
         * Configure listeners, timers, and tasks
         */
        // If is bungee, and not main, do nothing here except for the listeners
        if (!isBungeecordEnabled || isMainServer) {
            getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering commands"));
            registerCommands();
            getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering timers"));
            registerTimers();
        }

        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering listeners"));
        registerListeners();
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering tasks"));
        registerTasks();
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering placeholders"));
        new PlaceholderBukkitService().register();

        int pluginId = 12167;
        Metrics metrics = new Metrics(this, pluginId);
    }

    private void additionalConfigs() {
        /**
         * This is saving the config.yml (Default config)
         */
        try {
            // If bungeecord and not main server, don't save
            if (!isBungeecordEnabled || isMainServer) {
                getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Parsing mission and rewards config"));
                missionConfig = new MissionConfig();
                statsConfig = new BukkitConfig("datastore/stats.yml");
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

        // Remove the config if bungee && non-main
        if (isBungeecordEnabled && !isMainServer) {
            File dataFolder = getDataFolder();
            for (DbType dbType : DbType.values()) {
                if (!dbType.equals(DbType.MISSION_CACHE)) {
                    File file = new File(dataFolder, "datastore/" + dbType.name().toLowerCase(Locale.ROOT) + ".yml");
                    if (file.exists())
                        file.delete();
                }
            }

            File file = new File(dataFolder, "datastore/stats.yml");
            if (file.exists())
                file.delete();

            File missionFolder = new File(dataFolder, "missions");
            if (missionFolder.exists()) {
                for (String s : missionFolder.list()) {
                    File yaml = new File(missionFolder, s);
                    if (yaml.exists())
                        yaml.delete();
                }

                missionFolder.delete();
            }
        }
    }

    private void determineBungeeCord() {
        /**
         * Determine bungeecord is enabled, and whether this server is the main server
         */
        isMainServer = false;
        if (getInstanceConfig().getBoolean("bungeecord.enable")) {
            getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Running BUNGEECORD mode"));
            getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering Bungee Plugin Messaging Channel"));
            registerPMC();
            // This means that this bukkit instance should respond to the events
            getServer().getConsoleSender().sendMessage("BungeeCord detected and enabled");
            isBungeecordEnabled = true;

            isMainServer = getInstanceConfig().getBoolean("bungeecord.main-server");
        }
    }

    private void determineMissionEnabled() {
        /**
         * Determine whether the server admin configured the hooks correctly
         */
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Determining Enabled Mission Type"));
        Collection<String> keys = mainConfig.getKeys("mission.types");
        for (String key : keys) {
            MissionType missionType = MissionType.valueOf(key.toUpperCase(Locale.ROOT));

            if (mainConfig.getKeys("mission.types." + key + ".hooks") == null
                    || mainConfig.getKeys("mission.types." + key + ".hooks").size() == 0) {
                // This means that this mission does not have hooks, check enabled or not
                if (mainConfig.getBoolean("mission.types." + key + ".enable")) {
                    missionAndHooks.put(missionType, new ArrayList<>());
                }
            } else {
                // This means this type has hooks, one of the hooks has to be enabled for it to be enabled

                // Special corner case: RESOURCE needs to enabled vanilla with hooks
                if (missionType.equals(MissionType.RESOURCE) && mainConfig.getBoolean("mission.types." + key + ".enable")) {
                    missionAndHooks.put(MissionType.RESOURCE, new ArrayList<>());
                }

                Collection<String> hooks = mainConfig.getKeys("mission.types." + key + ".hooks");
                for (String hook : hooks) {
                    if (Bukkit.getPluginManager().isPluginEnabled(hook)
                            && mainConfig.getBoolean("mission.types." + key + ".hooks." + hook)
                            && mainConfig.getBoolean("mission.types." + key + ".enable")) {
                        System.out.println("Hook enabled");
                        if (missionAndHooks.containsKey(missionType)) {
                            List<String> hookList = missionAndHooks.get(missionType);
                            hookList.add(hook);
                            missionAndHooks.put(missionType, hookList);
                        } else {
                            List<String> hookList = new ArrayList<>();
                            hookList.add(hook);
                            missionAndHooks.put(missionType, hookList);
                        }
                    }
                }
            }
        }

        for (MissionType missionType : MissionType.values()) {
            if (missionAndHooks.containsKey(missionType)) {
                getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&d" + missionType + " &fis enabled!"));
            }
        }
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("=========   TOWNYMISSION DISABLING   =========");
        if (storageType == null) {
            return;
        }
        if (storageType.equals(StorageType.MYSQL)) {
            if (StorageService.getInstance().getDataSource() != null) {
                StorageService.getInstance().closeDb();
            }
        }
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
            rootCmd.registerCommand("deposit", new TownyMissionDeposit(this));
            rootCmd.registerCommand("claim", new TownyMissionClaim(this));
            rootCmd.registerCommand("info", new TownyMissionInfo(this));
            rootCmd.registerCommand("rank", new TownyMissionRank(this));
            rootCmd.registerCommand("reward", new TownyMissionReward(this));

            // Admin commands
            TownyMissionAdminRoot rootAdminCmd = new TownyMissionAdminRoot(this);
            this.getCommand("townymissionadmin").setExecutor(rootAdminCmd);
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
            if (isMissionEnabled(MissionType.EXPANSION)) {
                getServer().getConsoleSender().sendMessage("Hooked into Towny Expansion Events");
                getServer().getPluginManager().registerEvents(new ExpansionListener(this), this);
            }

            getServer().getPluginManager().registerEvents(new TownFallListener(this), this);
            getServer().getPluginManager().registerEvents(new DoMissionListener(this), this);

            // GUI listeners
            getServer().getPluginManager().registerEvents(new MissionManageGui(this), this);
        }

        if (isMissionEnabled(MissionType.MOB)) {
            getServer().getConsoleSender().sendMessage("Hooked into Vanilla Mob Events");
            getServer().getPluginManager().registerEvents(new MobListener(this), this);
        }

        if (isMissionEnabled(MissionType.MONEY)) {
            if (missionAndHooks.get(MissionType.MONEY).contains("CMI")) {
                getServer().getConsoleSender().sendMessage("Hooked into CMI Moeny Events");
                getServer().getPluginManager().registerEvents(new CMIMoneyListener(this), this);
            }
            if (missionAndHooks.get(MissionType.MONEY).contains("EssentialsX")) {
                getServer().getConsoleSender().sendMessage("Hooked into EssentialsX Money Events");
                getServer().getPluginManager().registerEvents(new EssentialMoneyListener(this), this);
            }
        }

        if (isMissionEnabled(MissionType.VOTE)) {
            getServer().getConsoleSender().sendMessage("Hooked into UltimateVotes Vote Events");
            getServer().getPluginManager().registerEvents(new VoteListener(this), this);
        }
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
        getServer().getConsoleSender().sendMessage("Started sprint timer");
        timerService.startSprintTimer();
        getServer().getConsoleSender().sendMessage("Started season timer");
        timerService.startSeasonTimer();
    }

    private void registerTasks() {
        if (!isMainServer()) {
            SendCachedMissionTask.registerTask();
            getServer().getConsoleSender().sendMessage("Started send cache task");
        }
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
        String langFile = "lang/" + mainConfig.getString("language") + ".yml";
        langConfig = new BukkitConfig(langFile);
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

    public String getLangEntryNoPrefix(String path) {
        return langConfig.getString(path);
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
    public TownyMissionConfig getInstanceConfig() {
        return mainConfig;
    }

    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    @Override
    public boolean isMainServer() {
        return !isBungeecordEnabled || isMainServer;
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

    public boolean isMissionEnabled(MissionType missionType) {
        return missionAndHooks.containsKey(missionType);
    }

    public List<String> getHooks(MissionType missionType) {
        return missionAndHooks.getOrDefault(missionType, null);
    }
}
