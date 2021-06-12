package world.naturecraft.townymission.data.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.components.entity.SprintEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Sprint database.
 */
public class SprintDatabase extends Database<SprintEntry> {

    private static SprintDatabase singleton;

    /**
     * Instantiates a new Sprint database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public SprintDatabase(HikariDataSource db, String tableName) {
        super(db, tableName);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintDatabase getInstance() {
        return singleton;
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` VARCHAR(255) NOT NULL ," +
                    "`town_id` VARCHAR(255) NOT NULL ," +
                    "`town_name` VARCHAR(255) NOT NULL, " +
                    "`naturepoints` INT NOT NULL, " +
                    "`sprint` INT NOT NULL ," +
                    "`season` INT NOT NULL ," +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<SprintEntry> getEntries() {
        List<SprintEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            try {
                ResultSet result = p.executeQuery();

                while (result.next()) {
                    SprintEntry entry = new SprintEntry(UUID.fromString(result.getString("id")),
                            result.getString("town_id"),
                            result.getString("town_name"),
                            result.getInt("naturepoints"),
                            result.getInt("sprint"),
                            result.getInt("season"));
                    list.add(entry);
                }
            } catch (SQLException e) {
                return null;
            }

            return null;
        });
        return list;
    }

    /**
     * Add.
     *
     * @param townUUID     the town uuid
     * @param townName     the town name
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    public void add(String townUUID, String townName, int naturePoints, int sprint, int season) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    townUUID + "', '" +
                    townName + "', '" +
                    naturePoints + "', '" +
                    sprint + "', '" +
                    season + "');";
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
     * @param id           the id
     * @param townUUID     the town uuid
     * @param townName     the town name
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    public void update(UUID id, String townUUID, String townName, int naturePoints, int sprint, int season) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET town_id='" + townUUID +
                    "', town_name='" + townName +
                    "', naturepoints='" + naturePoints +
                    "', sprint='" + sprint +
                    "', season='" + season +
                    "' WHERE id='" + id + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
