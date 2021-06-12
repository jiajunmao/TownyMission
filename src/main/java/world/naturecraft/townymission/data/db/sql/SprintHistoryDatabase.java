package world.naturecraft.townymission.data.db.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.components.containers.sql.SprintHistoryEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Sprint history database.
 */
public class SprintHistoryDatabase extends Database<SprintHistoryEntry> {

    private static SprintHistoryDatabase singleton;

    /**
     * Instantiates a new Sprint history database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public SprintHistoryDatabase(HikariDataSource db, String tableName) {
        super(db, tableName);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintHistoryDatabase getInstance() {
        return singleton;
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` VARCHAR(255) NOT NULL ," +
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
                list.add(new SprintHistoryEntry(UUID.fromString(result.getString("id")),
                        result.getInt("season"),
                        result.getInt("sprint"),
                        result.getLong("started_time"),
                        result.getString("rank_json")));
            }

            return null;
        });
        return list;
    }

    /**
     * Add.
     *
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void add(int season, int sprint, long startedTime, String rankJson) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid.toString() + "', '" +
                    season + "' , '" +
                    sprint + "' , '" +
                    startedTime + "' , '" +
                    rankJson + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Remove.
     *
     * @param id the id
     */
    public void remove(UUID id) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "id='" + id + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Update.
     *
     * @param id          the id
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(UUID id, int season, int sprint, long startedTime, String rankJson) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET season='" + season +
                    "', sprint='" + sprint +
                    "', started_time='" + startedTime +
                    "', rank_json='" + rankJson +
                    "' WHERE id='" + id + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
