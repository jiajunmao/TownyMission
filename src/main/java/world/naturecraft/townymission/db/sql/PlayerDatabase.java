package world.naturecraft.townymission.db.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.PlayerEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Player database.
 */
public class PlayerDatabase extends Database<PlayerEntry> {

    /**
     * Instantiates a new Player database.
     *
     * @param instance  the instance
     * @param db        the db
     * @param tableName the table name
     */
    public PlayerDatabase(TownyMission instance, HikariDataSource db, String tableName) {
        super(instance, db, tableName);
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` INT NOT NULL AUTO_INCREMENT ," +
                    "`uuid` VARCHAR(255) NOT NULL ," +
                    "`player_name` VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<PlayerEntry> getEntries() {
        List<PlayerEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                list.add(new PlayerEntry(resultSet.getInt("id"),
                        resultSet.getString("uuid"),
                        resultSet.getString("player_name")));
            }
            return null;
        });
        return list;
    }

    @Override
    public void add(PlayerEntry entry) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    entry.getUuid() + "', '" +
                    entry.getDisplayName() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public void remove(PlayerEntry entry) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "uuid='" + entry.getUuid() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
