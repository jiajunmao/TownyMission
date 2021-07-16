package world.naturecraft.townymission.data.source.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.DataEntity;
import world.naturecraft.townymission.components.entity.SprintEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The type Database.
 *
 * @param <T> the type parameter
 */
public abstract class SqlStorage<T extends DataEntity> {

    /**
     * The Table name.
     */
    protected boolean cached;
    protected HashMap<UUID, T> memCache;
    protected final String tableName;
    private final HikariDataSource db;

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

    protected void cacheData() {
        memCache = new HashMap<>();
        for (T entry : getEntries()) {
            memCache.put(entry.getId(), entry);
        }
        cached = true;
    }

    public void remove(UUID id) {
        if (cached) {
            memCache.remove(id);
            return;
        }

        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "uuid='" + id + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
