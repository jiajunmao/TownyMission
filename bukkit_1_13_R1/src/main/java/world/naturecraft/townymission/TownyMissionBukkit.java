package world.naturecraft.townymission;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.naturelib.InstanceType;
import world.naturecraft.naturelib.components.DataHolder;
import world.naturecraft.naturelib.components.enums.LangType;
import world.naturecraft.naturelib.components.enums.ServerType;
import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.naturelib.config.BukkitConfig;
import world.naturecraft.naturelib.config.NatureConfig;
import world.naturecraft.naturelib.exceptions.ConfigLoadingException;
import world.naturecraft.naturelib.exceptions.ConfigParsingException;
import world.naturecraft.naturelib.utils.BukkitUtil;
import world.naturecraft.naturelib.utils.UpdateChecker;
import world.naturecraft.townymission.commands.*;
import world.naturecraft.townymission.commands.admin.TownyMissionAdminInfo;
import world.naturecraft.townymission.commands.admin.TownyMissionAdminReload;
import world.naturecraft.townymission.commands.admin.TownyMissionAdminRoot;
import world.naturecraft.townymission.commands.admin.mission.TownyMissionAdminMissionAbort;
import world.naturecraft.townymission.commands.admin.mission.TownyMissionAdminMissionList;
import world.naturecraft.townymission.commands.admin.mission.TownyMissionAdminMissionRoot;
import world.naturecraft.townymission.commands.admin.season.*;
import world.naturecraft.townymission.commands.admin.sprint.TownyMissionAdminSprintPoint;
import world.naturecraft.townymission.commands.admin.sprint.TownyMissionAdminSprintRank;
import world.naturecraft.townymission.commands.admin.sprint.TownyMissionAdminSprintRoot;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.config.RewardConfigValidator;
import world.naturecraft.townymission.config.MissionConfig;
import world.naturecraft.townymission.data.dao.CooldownDao;
import world.naturecraft.townymission.gui.MissionManageGui;
import world.naturecraft.townymission.listeners.DoMissionListener;
import world.naturecraft.townymission.listeners.PMCListener;
import world.naturecraft.townymission.listeners.TownFallListener;
import world.naturecraft.townymission.listeners.UpdateRemindListener;
import world.naturecraft.townymission.listeners.mission.ExpansionListener;
import world.naturecraft.townymission.listeners.mission.MobListener;
import world.naturecraft.townymission.listeners.mission.VoteListener;
import world.naturecraft.townymission.listeners.mission.money.CMIMoneyListener;
import world.naturecraft.townymission.listeners.mission.money.EssentialMoneyListener;
import world.naturecraft.townymission.services.PlaceholderBukkitService;
import world.naturecraft.townymission.services.StorageService;
import world.naturecraft.townymission.services.TimerService;
import world.naturecraft.townymission.tasks.SendCachedMissionTask;

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
    private NatureConfig guiConfig;
    private NatureConfig statsConfig;
    private NatureConfig mainConfig;
    private NatureConfig langConfig;

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

        InstanceType.serverType = ServerType.BUKKIT;
        InstanceType.registerInstance(this);
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
        getServer().getConsoleSender().sendMessage("Loading language file " + langFile);
        langConfig = new BukkitConfig(langFile);
        langConfig.updateConfig();

        String guiLangFilePath = "gui/" + mainConfig.getString("language") + ".yml";
        guiConfig = new BukkitConfig(guiLangFilePath);
        guiConfig.updateConfig();

        File missionManageYml = new File("gui/mission_manage.yml");
        if (missionManageYml.exists()) {
            missionManageYml.delete();
        }

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
            StorageService.getInstance();
        } else {
            getServer().getConsoleSender().sendMessage("Using YAML flat file as storage backend");
        }

        /**
         * Migrate section
         */
        // If config version is one (automatically updated), migrate JsonList to JsonMap, and increment version number
        String ver = statsConfig.getString("config.version");
        if (ver == null || ver.equals("1")) {
            getInstanceLogger().warning("You are currently on version 1, migrating to version 2");
            List<CooldownEntry> cooldownEntries = CooldownDao.getInstance().getEntries();
            for (CooldownEntry entry : cooldownEntries) {
                entry.getCooldownJsonList().migrateListToMap();
                CooldownDao.getInstance().update(entry);
            }

            statsConfig.set("config.version", 2);
            statsConfig.save();
        }

        /**
         * Configure listeners, timers, and tasks
         */
        // If is bungee, and not main, do nothing here except for the listeners
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering commands"));
        registerCommands();
        if (!isBungeecordEnabled || isMainServer) {
            getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering timers"));
            registerTimers();
        }

        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering listeners"));
        registerListeners();
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering tasks"));
        registerTasks();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Registering placeholders"));
            new PlaceholderBukkitService().register();
        }

        // Update bStats
        int pluginId = 12167;
        Metrics metrics = new Metrics(this, pluginId);

        // Check for updates
        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor("&6===> Checking updates"));
        DataHolder<JavaPlugin> instanceHolder = new DataHolder<>(this);
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                new UpdateChecker(instanceHolder.getData(), 94472).getVersion(version -> {
                    version = version.substring(1);
                    if (!UpdateChecker.isGreater(version, getDescription().getVersion())) {
                        String str = "&bThere is a an update available! Please visit Spigot resource page to download! Current version: " + "&f" + instanceHolder.getData().getDescription().getVersion() + ", &bLatest version: " + "&f" + version;
                        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor(str));
                    } else {
                        String str = "&fThe version you are using is the latest, yay! Current version: " + getDescription().getVersion();
                        getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor(str));
                    }
                });
            }
        };
        r.runTaskAsynchronously(this);
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
                statsConfig.updateConfig();
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
        TownyMissionRoot rootCmd;
        if (!isBungeecordEnabled || isMainServer) {
            getServer().getConsoleSender().sendMessage("Registering main server commands");
            TownyMissionRoot root = new TownyMissionRoot(this);
            TownyMissionAdminRoot rootAdminCmd = new TownyMissionAdminRoot(this);
            TownyMissionAdminSeasonRoot seasonRoot = new TownyMissionAdminSeasonRoot(this);
            TownyMissionAdminSprintRoot sprintRoot = new TownyMissionAdminSprintRoot(this);
            TownyMissionAdminMissionRoot missionRoot = new TownyMissionAdminMissionRoot(this);

            rootCmd = root;
            this.getCommand("townymission").setExecutor(rootCmd);

            // User commands
            rootCmd.registerCommand("list", new TownyMissionList(this));
            rootCmd.registerCommand("deposit", new TownyMissionDeposit(this));
            rootCmd.registerCommand("claim", new TownyMissionClaim(this));
            rootCmd.registerCommand("info", new TownyMissionInfo(this));
            rootCmd.registerCommand("rank", new TownyMissionRank(this));
            rootCmd.registerCommand("reward", new TownyMissionReward(this));

            // Admin commands
            this.getCommand("townymissionadmin").setExecutor(rootAdminCmd);
            rootAdminCmd.registerCommand("reload", new TownyMissionAdminReload(this));
            rootAdminCmd.registerCommand("info", new TownyMissionAdminInfo(this));
            rootAdminCmd.registerCommand("season", seasonRoot);
            rootAdminCmd.registerCommand("sprint", sprintRoot);
            rootAdminCmd.registerCommand("mission", missionRoot);

            // Admin season commands
            seasonRoot.registerCommand("start", new TownyMissionAdminSeasonStart(this));
            seasonRoot.registerCommand("pause", new TownyMissionAdminSeasonPause(this));
            seasonRoot.registerCommand("point", new TownyMissionAdminSeasonPoint(this));
            seasonRoot.registerCommand("rank", new TownyMissionAdminSeasonRank(this));

            // Admin sprint commands
            sprintRoot.registerCommand("point", new TownyMissionAdminSprintPoint(this));
            sprintRoot.registerCommand("rank", new TownyMissionAdminSprintRank(this));

            // Admin mission commands
            missionRoot.registerCommand("list", new TownyMissionAdminMissionList(this));
            missionRoot.registerCommand("abort", new TownyMissionAdminMissionAbort(this));
        } else {
            getServer().getConsoleSender().sendMessage("Registering non-main server commands");
            rootCmd = new TownyMissionRoot(this);
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

            // Update listener
            getServer().getPluginManager().registerEvents(new UpdateRemindListener(this), this);
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
     * Reload configs.
     *
     * @throws ConfigLoadingException the config loading exception
     */
    public void reloadConfigs() throws ConfigLoadingException {
        mainConfig = new BukkitConfig("config.yml");
        String langFile = "lang/" + mainConfig.getString("language") + ".yml";
        langConfig = new BukkitConfig(langFile);
        guiConfig = new BukkitConfig("gui/" + mainConfig.getString("language") + ".yml");
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
    @Deprecated
    public String getLangEntry(String path) {
        String finalString = "";
        finalString += langConfig.getString("prefix") + " ";
        finalString += langConfig.getString(path);
        return finalString;
    }

    @Override
    public String getLangEntry(String s, boolean b) {
        if (b) {
            return langConfig.getString("prefix") + " " + langConfig.getString(s);
        } else {
            return langConfig.getString(s);
        }
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
    public NatureConfig getStatsConfig() {
        return statsConfig;
    }

    @Override
    public NatureConfig getInstanceConfig() {
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

    public String getGuiLangEntry(String path) {
        return guiConfig.getString(path);
    }

    public List<String> getGuiLangEntries(String path) {
        return new ArrayList<>(guiConfig.getStringList(path));
    }
}
