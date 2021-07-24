package world.naturecraft.townymission.data.source.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.DataEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The type Database.
 *
 * @param <T> the type parameter
 */
public abstract class SqlStorage<T extends DataEntity> {

    protected final String tableName;
    private final HikariDataSource db;
    /**
     * The Table name.
     */
    protected boolean cached;
    protected HashMap<UUID, T> memCache;

    /**
     * Instantiates a new Database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public SqlStorage(HikariDataSource db, String tableName) {
        this.db = db;
        this.tableName = tableName;
        this.cached = false;
        createTable();

        if (TownyMissionInstance.getInstance().getInstanceConfig().getBoolean("database.mem-cache")) {
            cacheData();
            cached = true;
        }
    }

    /**
     * Create table.
     */
    public abstract void createTable();

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public abstract List<T> getEntries();

    /**
     * Execute t.
     *
     * @param <T>      the type parameter
     * @param callback the callback
     * @return the t
     */
    public <T> T execute(ConnectionCallBack<T> callback) {
        try (Connection conn = db.getConnection()) {
            return callback.doConnection(conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Error during execution", e);
        }
    }

    public void cacheData() {
        memCache = new HashMap<>();
        for (T entry : getEntries()) {
            memCache.put(entry.getId(), entry);
        }
        cached = true;
    }

    public void remove(UUID id) {
        if (cached) {
            memCache.remove(id);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    removeRemote(id);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        removeRemote(id);
    }

    public void removeRemote(UUID id) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "uuid='" + id + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    public abstract void update(T data);

    public void writeBack() {
        if (!cached) return;

        List<T> list = new ArrayList<>(memCache.values());
        for (T entry : list) {
            update(entry);
        }
    }

    /**
     * The interface Connection call back.
     *
     * @param <T> the type parameter
     */
    public interface ConnectionCallBack<T> {
        /**
         * Do connection t.
         *
         * @param conn the conn
         * @return the t
         * @throws SQLException the sql exception
         */
        T doConnection(Connection conn) throws SQLException;
    }
}
