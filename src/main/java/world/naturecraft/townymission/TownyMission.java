package world.naturecraft.townymission;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import world.naturecraft.townymission.commands.*;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.gui.MissionManageGui;
import world.naturecraft.townymission.config.CustomConfigLoader;
import world.naturecraft.townymission.data.db.sql.*;
import world.naturecraft.townymission.listeners.external.MissionListener;
import world.naturecraft.townymission.listeners.external.TownFallListener;
import world.naturecraft.townymission.listeners.internal.DoMissionListener;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.services.TownyMissionService;
import world.naturecraft.townymission.utils.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The type Towny mission.
 */
public class TownyMission extends JavaPlugin {

    private final Logger logger = getLogger();
    private Map<DbType, Database> dbList;
    private Map<DbType, TownyMissionService> serviceList;
    private HikariDataSource db;
    private CustomConfigLoader customConfigLoader;
    private TownyMissionRoot rootCmd;
    private boolean enabled = true;

    @Override
    public void onEnable() {
        logger.info("=========   TOWNYMISSION LOADING   =========");
        this.saveDefaultConfig();
        try {
            customConfigLoader = new CustomConfigLoader(this);
        } catch (IOException e) {
            logger.severe("IO operation fault during custom config initialization");
            e.printStackTrace();
            enabled = false;
        } catch (InvalidConfigurationException e) {
            logger.severe("Invalid configuration during custom config initialization");
            e.printStackTrace();
            enabled = false;
        }

        logger.info("=========   CONNECTING TO TOWNYMISSION DATABASE   =========");
        dbList = new HashMap<>();
        serviceList = new HashMap<>();
        connect();
        registerDatabases();
        initializeDatabases();
        registerService();

        registerCommands();
        registerListeners();

        if (!enabled) {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        logger.info("=========   TOWNYMISSION DISABLING   =========");
        close();
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
     * Register service.
     */
    public void registerService() {
        serviceList.put(DbType.MISSION, new MissionService(this));
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
}
