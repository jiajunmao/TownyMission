package world.naturecraft.townymission.services;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.exceptions.DbConnectException;
import world.naturecraft.townymission.components.DataHolder;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.config.TownyMissionConfig;
import world.naturecraft.townymission.data.source.sql.SqlStorage;
import world.naturecraft.townymission.data.storage.Storage;
import world.naturecraft.townymission.utils.Util;

import java.util.HashMap;
import java.util.Locale;
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
        boolean cache = instance.getInstanceConfig().getBoolean("database.mem-cache");
        dbMap = new HashMap<>();
        initializeMap();

        if (storageType.equals(StorageType.MYSQL) && cache) {
            instance.getInstanceLogger().info("&f Memory caching databases");
            for (DbType dbType : DbType.values()) {
                ((SqlStorage) dbMap.get(dbType)).cacheData();
            }
        }
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
            if (instance.isMainServer() || dbType.equals(DbType.MISSION_CACHE)) {
                String packageName = "world.naturecraft.townymission.data.source." + storageType.name().toLowerCase(Locale.ROOT);
                String className = "";
                if (dbType.name().indexOf('_') != -1) {
                    int index = dbType.name().indexOf('_');
                    className += Util.capitalizeFirst(dbType.name().substring(0, index));
                    className += Util.capitalizeFirst(dbType.name().substring(index + 1));
                } else {
                    className += Util.capitalizeFirst(dbType.name());
                }
                className += Util.capitalizeFirst(storageType.name()) + "Storage";

                String fullPath = packageName + "." + className;

                try {
                    Storage storage = (Storage) Class.forName(fullPath).newInstance();
                    dbMap.put(dbType, storage);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
