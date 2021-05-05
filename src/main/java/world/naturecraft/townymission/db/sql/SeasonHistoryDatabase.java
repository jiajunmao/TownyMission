package world.naturecraft.townymission.db.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.containers.sql.SeasonHistoryEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SeasonHistoryDatabase extends Database<SeasonHistoryEntry> {

    public SeasonHistoryDatabase(TownyMission instance, HikariDataSource db, String tableName) {
        super(instance, db, tableName);
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` INT NOT NULL AUTO_INCREMENT ," +
                    "`season` INT NOT NULL ," +
                    "`started_time` BIGINT NOT NULL ," +
                    "`rank_json` VARCHAR(255) NOT NULL ," +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<SeasonHistoryEntry> getEntries() {
        List<SeasonHistoryEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName;
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();

            while(result.next()) {
                list.add(new SeasonHistoryEntry(result.getInt("id"),
                        result.getInt("season"),
                        result.getLong("started_time"),
                        result.getString("rank_json")));
            }

            return null;
        });
        return list;
    }

    @Override
    public void add(SeasonHistoryEntry entry) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    entry.getSeason() + "', '" +
                    entry.getStartTime() + "', '" +
                    entry.getRankJson() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public void remove(SeasonHistoryEntry entry) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "id='" + entry.getId() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
