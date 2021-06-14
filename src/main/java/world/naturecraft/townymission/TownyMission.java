package world.naturecraft.townymission;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import world.naturecraft.townymission.api.exceptions.ConfigLoadingError;
import world.naturecraft.townymission.commands.*;
import world.naturecraft.townymission.commands.admin.TownyMissionAdminListMissions;
import world.naturecraft.townymission.commands.admin.TownyMissionAdminReload;
import world.naturecraft.townymission.commands.admin.TownyMissionAdminRoot;
import world.naturecraft.townymission.commands.admin.TownyMissionAdminStartSeason;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.components.gui.MissionManageGui;
import world.naturecraft.townymission.config.StatsConfigLoader;
import world.naturecraft.townymission.config.mission.MissionConfigLoader;
import world.naturecraft.townymission.listeners.external.MissionListener;
import world.naturecraft.townymission.listeners.external.TownFallListener;
import world.naturecraft.townymission.listeners.internal.DoMissionListener;
import world.naturecraft.townymission.services.TimerService;
import world.naturecraft.townymission.utils.Util;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * The type Towny mission.
 */
public class TownyMission extends JavaPlugin {

    private final Logger logger = getLogger();
    private HikariDataSource db;
    private MissionConfigLoader missionConfigLoader;
    private StatsConfigLoader statsConfigLoader;
    private TownyMissionRoot rootCmd;
    private StorageType storageType;
    private boolean enabled = true;

    @Override
    public void onEnable() {
        logger.info(Util.translateColor("{#22DDBA}" + "  _______                        __  __ _         _             "));
        logger.info(Util.translateColor("{#22DDBA}" + " |__   __|                      |  \\/  (_)       (_)            "));
        logger.info(Util.translateColor("{#22DDBA}" + "    | | _____      ___ __  _   _| \\  / |_ ___ ___ _  ___  _ __  "));
        logger.info(Util.translateColor("{#22DDBA}" + "    | |/ _ \\ \\ /\\ / / '_ \\| | | | |\\/| | / __/ __| |/ _ \\| '_ \\ "));
        logger.info(Util.translateColor("{#22DDBA}" + "    | | (_) \\ V  V /| | | | |_| | |  | | \\__ \\__ \\ | (_) | | | |"));
        logger.info(Util.translateColor("{#22DDBA}" + "    |_|\\___/ \\_/\\_/ |_| |_|\\__, |_|  |_|_|___/___/_|\\___/|_| |_|"));
        logger.info(Util.translateColor("{#22DDBA}" + "                            __/ |                               "));
        logger.info(Util.translateColor("{#22DDBA}" + "                           |___/                                "));
        logger.info("-----------------------------------------------------------------");

        logger.info(Util.translateColor("{#E9B728}===> Parsing configuration"));
        /**
         * This is saving the config.yml (Default config)
         */
        this.saveDefaultConfig();
        try {
            missionConfigLoader = new MissionConfigLoader(this);
            statsConfigLoader = new StatsConfigLoader(this);
        } catch (IOException | InvalidConfigurationException e) {
            logger.severe("IO operation fault during custom config initialization");
            e.printStackTrace();
            enabled = false;
        }

        /**
         * Configure data storage, yaml, or mysql
         */
        logger.info(Util.translateColor("{#E9B728}===> Connecting to datastore"));
        String storage = getConfig().getString("storage");
        storageType = StorageType.valueOf(storage.toUpperCase(Locale.ROOT));

        if (storageType.equals(StorageType.MYSQL)) {
            logger.info("Using MYSQL as storage backend");
            connect();
        } else {
            logger.info("Using YAML flat file as storage backend");
        }

        logger.info(Util.translateColor("{#E9B728}===> Registering commands"));
        registerCommands();
        logger.info(Util.translateColor("{#E9B728}===> Registering listeners"));
        registerListeners();
        logger.info(Util.translateColor("{#E9B728}===> Registering timers"));
        registerTimers();

        if (!enabled) {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        logger.info("=========   TOWNYMISSION DISABLING   =========");
        if (storageType.equals(StorageType.MYSQL)) {
            close();
        }
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
        String dbAddress = getConfig().getString("database.address");
        String dbPort = getConfig().getString("database.port");
        String dbName = getConfig().getString("database.name");
        String dbUsername = getConfig().getString("database.username");
        String dbPassword = getConfig().getString("database.password");

        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", dbAddress);
        config.addDataSourceProperty("port", dbPort);
        config.addDataSourceProperty("databaseName", dbName);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);


        db = new HikariDataSource(config);
    }

    /**
     * Close.
     */
    public void close() {
        db.close();
    }

    /**
     * Gets custom config.
     *
     * @return the custom config
     */
    public MissionConfigLoader getCustomConfig() {
        return missionConfigLoader;
    }

    /**
     * Gets lang entry.
     *
     * @param path the path
     * @return the lang entry
     */
    public String getLangEntry(String path) {
        String finalString = "";
        finalString += getCustomConfig().getLangConfig().getString("prefix") + " ";
        finalString += getCustomConfig().getLangConfig().getString(path);
        return finalString;
    }

    /**
     * Reload configs.
     */
    public void reloadConfigs() {
        this.reloadConfig();
        try {
            missionConfigLoader = new MissionConfigLoader(this);
            statsConfigLoader = new StatsConfigLoader(this);
        } catch (IOException | InvalidConfigurationException e) {
            throw new ConfigLoadingError(e);
        }
    }

    /**
     * Gets storage type.
     *
     * @return the storage type
     */
    public StorageType getStorageType() {
        return storageType;
    }

    /**
     * Gets datasource.
     *
     * @return the datasource
     */
    public HikariDataSource getDatasource() {
        return db;
    }

    public StatsConfigLoader getStatsConfig() {
        return statsConfigLoader;
    }
}
