package world.naturecraft.townymission.services;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.exceptions.DbConnectException;
import world.naturecraft.townymission.components.DataHolder;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.config.TownyMissionConfig;
import world.naturecraft.townymission.data.db.Storage;
import world.naturecraft.townymission.data.sql.*;
import world.naturecraft.townymission.data.yaml.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * The type Storage service.
 */
public class StorageService {

    private static StorageService singleton;
    private final StorageType storageType;
    private final Map<DbType, Storage> dbMap;
    private final TownyMissionInstance instance;
    private HikariDataSource dataSource;

    /**
     * Instantiates a new Storage service.
     */
    public StorageService() {
        this.instance = TownyMissionInstance.getInstance();
        this.storageType = instance.getStorageType();
        dbMap = new HashMap<>();
        initializeMap();
    }


    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static StorageService getInstance() {
        if (singleton == null) {
            singleton = new StorageService();
        }

        return singleton;
    }

    /**
     * Connect db.
     *
     * @throws DbConnectException the db connect exception
     */
    public void connectDb() throws DbConnectException {

        TownyMissionConfig mainConfig = instance.getInstanceConfig();
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

    /**
     * Close db.
     */
    public void closeDb() {
        dataSource.close();
    }

    /**
     * Gets storage.
     *
     * @param <T>    the type parameter
     * @param dbType the db type
     * @return the storage
     */
    @SuppressWarnings("unchecked")
    public <T extends Storage> T getStorage(DbType dbType) {
        return (T) dbMap.get(dbType);
    }

    /**
     * Gets data source.
     *
     * @return the data source
     */
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Initialize map.
     */
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
                    break;
                case MISSION:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.MISSION, MissionYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.MISSION, MissionSqlStorage.getInstance());
                    break;
                case COOLDOWN:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.COOLDOWN, CooldownYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.COOLDOWN, CooldownSqlStorage.getInstance());
                    break;
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
                    break;
                case MISSION_HISTORY:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.MISSION_HISTORY, MissionHistoryYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.MISSION_HISTORY, MissionHistorySqlStorage.getInstance());
                    break;
                case MISSION_CACHE:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.MISSION_CACHE, MissionCacheYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.MISSION_CACHE, MissionCacheSqlStorage.getInstance());
                    break;
            }
        }
    }

}
