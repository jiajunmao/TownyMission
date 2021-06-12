package world.naturecraft.townymission;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import world.naturecraft.townymission.api.exceptions.ConfigLoadingError;
import world.naturecraft.townymission.commands.*;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.components.gui.MissionManageGui;
import world.naturecraft.townymission.config.CustomConfigLoader;
import world.naturecraft.townymission.data.sql.*;
import world.naturecraft.townymission.listeners.external.MissionListener;
import world.naturecraft.townymission.listeners.external.TownFallListener;
import world.naturecraft.townymission.listeners.internal.DoMissionListener;
import world.naturecraft.townymission.services.TimerService;
import world.naturecraft.townymission.utils.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The type Towny mission.
 */
public class TownyMission extends JavaPlugin {

    private final Logger logger = getLogger();
    private Map<DbType, Database> dbList;
    private HikariDataSource db;
    private CustomConfigLoader customConfigLoader;
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


        /**
         * This is saving the config.yml (Default config)
         */
        this.saveDefaultConfig();
        try {
            customConfigLoader = new CustomConfigLoader(this);
        } catch (IOException | InvalidConfigurationException e) {
            logger.severe("IO operation fault during custom config initialization");
            e.printStackTrace();
            enabled = false;
        }

        /**
         * Configure data storage, yaml, or mysql
         */
        logger.info("===> Connecting to database");
        String storage = getConfig().getString("storage");
        storageType = StorageType.valueOf(storage.toUpperCase(Locale.ROOT));

        if (storageType.equals(StorageType.MYSQL)) {
            dbList = new HashMap<>();
            connect();
            registerDatabases();
            initializeDatabases();
        }

        logger.info("===> Registering commands");
        registerCommands();
        logger.info("===> Registering listeners");
        registerListeners();
        logger.info("===> Registering timers");
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
     * Register databases.
     */
    public void registerDatabases() {
        dbList.put(DbType.MISSION, new MissionDatabase(db, Util.getDbName(DbType.MISSION)));
        dbList.put(DbType.MISSION_HISTORY, new MissionHistoryDatabase(db, Util.getDbName(DbType.MISSION_HISTORY)));
        dbList.put(DbType.SPRINT, new SprintDatabase(db, Util.getDbName(DbType.SPRINT)));
        dbList.put(DbType.SPRINT_HISTORY, new SprintHistoryDatabase(db, Util.getDbName(DbType.SPRINT_HISTORY)));
        dbList.put(DbType.SEASON, new SeasonDatabase(db, Util.getDbName(DbType.SEASON)));
        dbList.put(DbType.SEASON_HISTORY, new SeasonHistoryDatabase(db, Util.getDbName(DbType.SEASON_HISTORY)));
        dbList.put(DbType.COOLDOWN, new CooldownDatabase(db, Util.getDbName(DbType.COOLDOWN)));
    }

    /**
     * Register commands.
     */
    public void registerCommands() {
        this.rootCmd = new TownyMissionRoot(this);
        this.getCommand("townymission").setExecutor(rootCmd);

        rootCmd.registerCommand("listAll", new TownyMissionListAll(this));
        rootCmd.registerCommand("list", new TownyMissionList(this));
        rootCmd.registerCommand("start", new TownyMissionStart(this));
        rootCmd.registerCommand("abort", new TownyMissionAbort(this));
        rootCmd.registerCommand("deposit", new TownyMissionDeposit(this));
        rootCmd.registerCommand("claim", new TownyMissionClaim(this));
        rootCmd.registerCommand("info", new TownyMissionInfo(this));
        rootCmd.registerCommand("rank", new TownyMissionRank(this));
        rootCmd.registerCommand("reload", new TownyMissionReload(this));
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
     * Initialize databases.
     */
    public void initializeDatabases() {
        for (Database db : dbList.values()) {
            db.createTable();
        }
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

        db = new HikariDataSource();
        db.setMaximumPoolSize(5);
        db.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        db.addDataSourceProperty("serverName", dbAddress);
        db.addDataSourceProperty("port", dbPort);
        db.addDataSourceProperty("databaseName", dbName);
        db.addDataSourceProperty("user", dbUsername);
        db.addDataSourceProperty("password", dbPassword);
        db.setMinimumIdle(5);
        db.setConnectionTimeout(10000);
        db.setIdleTimeout(600000);
        db.setMaxLifetime(1800000);
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
    public CustomConfigLoader getCustomConfig() {
        return customConfigLoader;
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
            customConfigLoader = new CustomConfigLoader(this);
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
}
