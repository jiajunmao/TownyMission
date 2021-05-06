package world.naturecraft.townymission.db.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Task database.
 */
public class TaskDatabase extends Database<TaskEntry> {

    /**
     * Instantiates a new Task database.
     *
     * @param instance  the instance
     * @param db        the db
     * @param tableName the table name
     */
    public TaskDatabase(TownyMission instance, HikariDataSource db, String tableName) {
        super(instance, db, tableName);
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` INT NOT NULL AUTO_INCREMENT ," +
                    "`task_type` VARCHAR(255) NOT NULL ," +
                    "`added_time` BIGINT NOT NULL, " +
                    "`started_time` BIGINT NOT NULL, " +
                    "`allowed_time` BIGINT NOT NULL, " +
                    "`task_json` VARCHAR(255) NOT NULL ," +
                    "`town` VARCHAR(255) NOT NULL ," +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<TaskEntry> getEntries() {
        List<TaskEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();

            while (result.next()) {
                list.add(new TaskEntry(result.getInt("id"),
                        result.getString("task_type"),
                        result.getLong("added_time"),
                        result.getLong("started_time"),
                        result.getLong("allowed_time"),
                        result.getString("task_json"),
                        result.getString("town")));
            }
            return null;
        });
        return list;
    }

    @Override
    public void add(TaskEntry entry) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    entry.getTaskType() + "', '" +
                    entry.getAddedTime() + "', '" +
                    entry.getStartedTime() + "', '" +
                    entry.getAllowedTime() + "', '" +
                    entry.getTaskJson() + "', '" +
                    entry.getTown() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public void remove(TaskEntry entry) {
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
    public void update(TaskEntry entry) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET task_type='" + entry.getTaskType() +
                    "' AND added_time='" + entry.getAddedTime() +
                    "' AND started_time='" + entry.getStartedTime() +
                    "' AND allowed_time='" + entry.getAllowedTime() +
                    "' AND task_json='" + entry.getTaskJson() +
                    "' AND town='" + entry.getTown() +
                    "' WHERE id='" + entry.getId() + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
