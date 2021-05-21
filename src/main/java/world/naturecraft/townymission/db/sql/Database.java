package world.naturecraft.townymission.db.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * The type Database.
 *
 * @param <T> the type parameter
 */
public abstract class Database<T> {

    /**
     * The Table name.
     */
    protected final String tableName;
    private final TownyMission instance;
    private final HikariDataSource db;

    /**
     * Instantiates a new Database.
     *
     * @param instance  the instance
     * @param db        the db
     * @param tableName the table name
     */
    public Database(TownyMission instance, HikariDataSource db, String tableName) {
        this.instance = instance;
        this.db = db;
        this.tableName = tableName;
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
}
