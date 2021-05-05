package world.naturecraft.townymission;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import world.naturecraft.townymission.db.sql.*;
import world.naturecraft.townymission.enums.DbType;
import world.naturecraft.townymission.utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TownyMission extends JavaPlugin {

    private Logger logger = Bukkit.getLogger();
    private Map<DbType, Database> dbList;
    private HikariDataSource db;

    @Override
    public void onEnable() {
        logger.info("=========   RPG SPAWNER LOADING   =========");
        this.saveDefaultConfig();
        this.saveResource("missions", false);
        logger.info("=========   CONNECTING TO RPG SPAWNER DATABASE   =========");
        dbList = new HashMap<>();
        connect();
        registerDatabases();
        initializeDatabases();
    }

    @Override
    public void onDisable() {
        close();
    }

    public void registerDatabases() {
        register(DbType.PLAYER, new PlayerDatabase(this, db, Util.getDbName(DbType.PLAYER)));
        register(DbType.TASK, new TaskDatabase(this, db, Util.getDbName(DbType.TASK)));
        register(DbType.TASK_HISTORY, new TaskHistoryDatabase(this, db, Util.getDbName(DbType.TASK_HISTORY)));
        register(DbType.SPRINT, new SprintDatabase(this, db, Util.getDbName(DbType.SPRINT)));
        register(DbType.SPRINT_HISTORY, new SprintHistoryDatabase(this, db, Util.getDbName(DbType.SPRINT_HISTORY)));
        register(DbType.SEASON, new SeasonDatabase(this, db, Util.getDbName(DbType.SEASON)));
        register(DbType.SEASON_HISTORY, new SeasonHistoryDatabase(this, db, Util.getDbName(DbType.SEASON_HISTORY)));
    }

    public void initializeDatabases() {
        for(Database db : dbList.values()) {
            db.createTable();
        }
    }

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

    public void close() {
        db.close();
    }

    public void register(DbType dbType, Database database) {
        dbList.put(dbType, database);
    }
}
