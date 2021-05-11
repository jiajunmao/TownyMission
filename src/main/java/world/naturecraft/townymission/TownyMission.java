package world.naturecraft.townymission;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import world.naturecraft.townymission.commands.*;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.config.CustomConfigLoader;
import world.naturecraft.townymission.dao.Dao;
import world.naturecraft.townymission.db.sql.*;
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
        connect();
        registerDatabases();
        initializeDatabases();

        registerCommands();

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
        register(DbType.PLAYER, new PlayerDatabase(this, db, Util.getDbName(DbType.PLAYER)));
        register(DbType.TASK, new TaskDatabase(this, db, Util.getDbName(DbType.TASK)));
        register(DbType.TASK_HISTORY, new TaskHistoryDatabase(this, db, Util.getDbName(DbType.TASK_HISTORY)));
        register(DbType.SPRINT, new SprintDatabase(this, db, Util.getDbName(DbType.SPRINT)));
        register(DbType.SPRINT_HISTORY, new SprintHistoryDatabase(this, db, Util.getDbName(DbType.SPRINT_HISTORY)));
        register(DbType.SEASON, new SeasonDatabase(this, db, Util.getDbName(DbType.SEASON)));
        register(DbType.SEASON_HISTORY, new SeasonHistoryDatabase(this, db, Util.getDbName(DbType.SEASON_HISTORY)));
    }

    public void registerCommands() {
        this.rootCmd = new TownyMissionRoot(this);
        this.getCommand("townymission").setExecutor(rootCmd);

        rootCmd.registerCommand("listAll", new TownyMissionListAll(this));
        rootCmd.registerCommand("list", new TownyMissionList(this));
        rootCmd.registerCommand("start", new TownyMissionStart(this));
        rootCmd.registerCommand("abort", new TownyMissionAbort(this));
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
    }

    /**
     * Close.
     */
    public void close() {
        db.close();
    }

    /**
     * Register.
     *
     * @param dbType   the db type
     * @param database the database
     */
    public void register(DbType dbType, Database database) {
        dbList.put(dbType, database);
    }

    public CustomConfigLoader getCustomConfig() {
        return customConfigLoader;
    }

    public Database getDb(DbType dbType) {
        return dbList.get(dbType);
    }
}
