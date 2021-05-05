package world.naturecraft.townymission.db.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class Database<T> {

    private final TownyMission instance;
    private HikariDataSource db;
    protected final String tableName;

    public Database(TownyMission instance, HikariDataSource db, String tableName) {
        this.instance = instance;
        this.db = db;
        this.tableName = tableName;
    }

    public abstract void createTable();

    public abstract List<T> getEntries();

    public abstract void add(T entry);

    public abstract void remove(T entry);

    public <T> T execute(ConnectionCallBack<T> callback) {
        try (Connection conn = db.getConnection()) {
            return callback.doConnection(conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Error during execution", e);
        }
    }

    public interface ConnectionCallBack<T> {
        T doConnection(Connection conn) throws SQLException;
    }
}
