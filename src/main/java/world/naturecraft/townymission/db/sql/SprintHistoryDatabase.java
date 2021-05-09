package world.naturecraft.townymission.db.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SprintHistoryEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Sprint history database.
 */
public class SprintHistoryDatabase extends Database<SprintHistoryEntry> {

    /**
     * Instantiates a new Sprint history database.
     *
     * @param instance  the instance
     * @param db        the db
     * @param tableName the table name
     */
    public SprintHistoryDatabase(TownyMission instance, HikariDataSource db, String tableName) {
        super(instance, db, tableName);
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` INT NOT NULL AUTO_INCREMENT ," +
                    "`season` INT NOT NULL ," +
                    "`sprint` INT NOT NULL, " +
                    "`started_time` BIGINT NOT NULL, " +
                    "`rank_json` VARCHAR(255) NOT NULL ," +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<SprintHistoryEntry> getEntries() {
        List<SprintHistoryEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName;
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();

            while (result.next()) {
                list.add(new SprintHistoryEntry(result.getInt("id"),
                        result.getInt("season"),
                        result.getInt("sprint"),
                        result.getLong("started_time"),
                        result.getString("rank_json")));
            }

            return null;
        });
        return list;
    }

    @Override
    public void add(SprintHistoryEntry entry) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    entry.getSeason() + "' , '" +
                    entry.getSprint() + "' , '" +
                    entry.getStartedTime() + "' , '" +
                    entry.getRankJson() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public void remove(SprintHistoryEntry entry) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "id='" + entry.getId() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Update.
     *
     * @param entry the entry
     */
    @Override
    public void update(SprintHistoryEntry entry) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET season='" + entry.getSeason() +
                    "', sprint='" + entry.getSprint() +
                    "', started_time='" + entry.getStartedTime() +
                    "', rank_json='" + entry.getRankJson() +
                    "' WHERE id='" + entry.getId() + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
