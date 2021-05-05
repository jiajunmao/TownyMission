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
                list.add(new SprintEntry(result.getInt("id"),
                        result.getString("town_id"),
                        result.getString("town_name"),
                        result.getInt("naturepoints"),
                        result.getInt("sprint"),
                        result.getInt("season")));
            }
            return null;
        });
        return list;
    }

    @Override
    public void add(SprintEntry entry) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    entry.getTownID() + "', '" +
                    entry.getTownName() + "', '" +
                    entry.getNaturepoints() + "', '" +
                    entry.getSprint() + "', '" +
                    entry.getSeason() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public void remove(SprintEntry entry) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "id='" + entry.getId() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
