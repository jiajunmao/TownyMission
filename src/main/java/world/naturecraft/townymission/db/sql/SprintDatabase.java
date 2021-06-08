package world.naturecraft.townymission.db.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SprintEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Sprint database.
 */
public class SprintDatabase extends Database<SprintEntry> {

    /**
     * Instantiates a new Sprint database.
     *
     * @param instance  the instance
     * @param db        the db
     * @param tableName the table name
     */
    public SprintDatabase(TownyMission instance, HikariDataSource db, String tableName) {
        super(instance, db, tableName);
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` INT NOT NULL AUTO_INCREMENT ," +
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
            ResultSet result = p.executeQuery();

            while (result.next()) {
                SprintEntry entry = new SprintEntry(result.getInt("id"),
                        result.getString("town_id"),
                        result.getString("town_name"),
                        result.getInt("naturepoints"),
                        result.getInt("sprint"),
                        result.getInt("season"));
                list.add(entry);
                System.out.println("SprintDb parse in id: " + result.getInt("id") + " with entry id: " + entry.getId());
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
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
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
    public void remove(int id) {
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
    public void update(int id, String townUUID, String townName, int naturePoints, int sprint, int season) {
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
