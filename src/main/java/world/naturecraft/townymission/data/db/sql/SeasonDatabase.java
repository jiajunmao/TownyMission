package world.naturecraft.townymission.data.db.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Season database.
 */
public class SeasonDatabase extends Database<SeasonEntry> {

    /**
     * Instantiates a new Season database.
     *
     * @param instance  the instance
     * @param db        the db
     * @param tableName the table name
     */
    public SeasonDatabase(TownyMission instance, HikariDataSource db, String tableName) {
        super(instance, db, tableName);
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` INT NOT NULL AUTO_INCREMENT ," +
                    "`town_id` VARCHAR(255) NOT NULL ," +
                    "`town_name` VARCHAR(255) NOT NULL, " +
                    "`seasonpoints` INT NOT NULL, " +
                    "`season` INT NOT NULL ," +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<SeasonEntry> getEntries() {
        List<SeasonEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            try {
                ResultSet result = p.executeQuery();
                while (result.next()) {
                    list.add(new SeasonEntry(result.getInt("id"),
                            result.getString("town_id"),
                            result.getString("town_name"),
                            result.getInt("seasonpoints"),
                            result.getInt("season")));
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
        return list;
    }

    /**
     * Add.
     *
     * @param townUUID    the town uuid
     * @param townName    the town name
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void add(String townUUID, String townName, int seasonPoint, int season) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    townUUID + "', '" +
                    townName + "', '" +
                    seasonPoint + "', '" +
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
     * @param id          the id
     * @param townUUID    the town uuid
     * @param townName    the town name
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void update(int id, String townUUID, String townName, int seasonPoint, int season) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET town_id='" + townUUID +
                    "', town_name='" + townName +
                    "', seasonpoints='" + seasonPoint +
                    "', season='" + season +
                    "' WHERE id='" + id + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
