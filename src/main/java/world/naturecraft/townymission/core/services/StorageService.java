package world.naturecraft.townymission.core.services;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.exceptions.DbConnectException;
import world.naturecraft.townymission.core.components.DataHolder;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.config.MainConfig;
import world.naturecraft.townymission.core.data.db.Storage;
import world.naturecraft.townymission.core.data.sql.*;
import world.naturecraft.townymission.core.data.yaml.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class StorageService {

    private static StorageService singleton;
    private HikariDataSource dataSource;
    private StorageType storageType;
    private Map<DbType, Storage> dbMap;
    private TownyMissionInstance instance;

    public StorageService() {
        this.instance = TownyMissionInstance.getInstance();
        this.storageType = instance.getStorageType();
        dbMap = new HashMap<>();
        initializeMap();
    }


    public static StorageService getInstance() {
        if (singleton == null) {
            singleton = new StorageService();
        }

        return singleton;
    }

    public void connectDb() throws DbConnectException{

        MainConfig mainConfig = instance.getInstanceConfig();
        String dbAddress = mainConfig.getString("database.address");
        String dbPort = mainConfig.getString("database.port");
        String dbName = mainConfig.getString("database.name");
        String dbUsername = mainConfig.getString("database.username");
        String dbPassword = mainConfig.getString("database.password");

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
        config.setMaxLifetime(180000);

        final DataHolder<HikariDataSource> holder = new DataHolder<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                holder.setData(new HikariDataSource(config));
                return "connected";
            }
        });

        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            future.cancel(true);
            throw new DbConnectException(e);
        }

        this.dataSource = holder.getData();
    }

    public void closeDb() {
        dataSource.close();
    }

    @SuppressWarnings("unchecked")
    public <T extends Storage> T getStorage(DbType dbType) {
        TownyMissionBukkit townyMissionBukkit = (TownyMissionBukkit) Bukkit.getPluginManager().getPlugin("TownyMission");
        return (T) dbMap.get(dbType);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void initializeMap() {
        for (DbType dbType : DbType.values()) {
            switch (dbType) {
                case SEASON:
                    if (storageType.equals(StorageType.MYSQL))
                        dbMap.put(DbType.SEASON, SeasonSqlStorage.getInstance());
                    else
                        dbMap.put(DbType.SEASON, SeasonYamlStorage.getInstance());
                    break;
                case SPRINT:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.SPRINT, SprintYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.SPRINT, SprintSqlStorage.getInstance());
                    break;
                case CLAIM:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.CLAIM, ClaimYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.CLAIM, ClaimSqlStorage.getInstance());
                case MISSION:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.MISSION, MissionYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.MISSION, MissionSqlStorage.getInstance());
                case COOLDOWN:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.COOLDOWN, CooldownYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.COOLDOWN, CooldownSqlStorage.getInstance());
                case SEASON_HISTORY:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.SEASON_HISTORY, SeasonHistoryYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.SEASON_HISTORY, SeasonHistorySqlStorage.getInstance());
                case SPRINT_HISTORY:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.SPRINT_HISTORY, SprintHistoryYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.SPRINT_HISTORY, SprintHistorySqlStorage.getInstance());
                case MISSION_HISTORY:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.MISSION_HISTORY, MissionHistoryYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.MISSION_HISTORY, MissionHistorySqlStorage.getInstance());
            }
        }
    }

}
