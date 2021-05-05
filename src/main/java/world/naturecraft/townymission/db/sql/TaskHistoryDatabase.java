package world.naturecraft.townymission.db;

import io.lumine.mythic.utils.storage.sql.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.containers.TaskHistoryEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TaskHistoryDatabase extends Database<TaskHistoryEntry> {

    public TaskHistoryDatabase(TownyMission instance, HikariDataSource db, String tableName) {
        super(instance, db, tableName);
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` INT NOT NULL AUTO_INCREMENT ," +
                    "`task_type` VARCHAR(255) NOT NULL ," +
                    "`started_time` BIGINT NOT NULL, " +
                    "`allowed_time` BIGINT NOT NULL, " +
                    "`task_json` VARCHAR(255) NOT NULL ," +
                    "`town` VARCHAR(255) NOT NULL ," +
                    "`completed_time` BIGINT NOT NULL, " +
                    "`sprint` INT NOT NULL, " +
                    "`season` INT NOT NULL, " +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<TaskHistoryEntry> getEntries() {
        List<TaskHistoryEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();

            while(result.next()) {
                list.add(new TaskHistoryEntry(result.getInt("id"),
                        result.getString("task_type"),
                        result.getLong("started_time"),
                        result.getLong("allowed_time"),
                        result.getString("task_json"),
                        result.getString("town"),
                        result.getLong("completed_time"),
                        result.getInt("sprint"),
                        result.getInt("season")));
            }
            return null;
        });
        return list;
    }

    @Override
    public void add(TaskHistoryEntry entry) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    entry.getTaskType() + "', '" +
                    entry.getStartedTime() + "', '" +
                    entry.getAllowedTime() + "', '" +
                    entry.getTaskJson() + "', '" +
                    entry.getTown() + "', '" +
                    entry.getCompletedTime() + "', '" +
                    entry.getSprint() + "', '" +
                    entry.getSeason() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public void remove(TaskHistoryEntry entry) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "id='" + entry.getId() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
