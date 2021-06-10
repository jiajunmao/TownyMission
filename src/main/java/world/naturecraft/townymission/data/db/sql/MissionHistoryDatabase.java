package world.naturecraft.townymission.data.db.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.MissionHistoryEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Task history database.
 */
public class MissionHistoryDatabase extends Database<MissionHistoryEntry> {

    private static MissionHistoryDatabase singleton = null;

    /**
     * Instantiates a new Task history database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public MissionHistoryDatabase(HikariDataSource db, String tableName) {
        super(db, tableName);
        singleton = this;
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
                    "`started_player` VARCHAR(255) NOT NULL ," +
                    "`completed_time` BIGINT NOT NULL, " +
                    "`claimed` BOOLEAN NOT NULL, " +
                    "`sprint` INT NOT NULL, " +
                    "`season` INT NOT NULL, " +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<MissionHistoryEntry> getEntries() {
        List<MissionHistoryEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();

            while (result.next()) {
                try {
                    list.add(new MissionHistoryEntry(result.getInt("id"),
                            result.getString("task_type"),
                            result.getLong("added_time"),
                            result.getLong("started_time"),
                            result.getLong("allowed_time"),
                            result.getString("task_json"),
                            result.getString("town"),
                            result.getString("started_player"),
                            result.getLong("completed_time"),
                            result.getBoolean("claimed"),
                            result.getInt("sprint"),
                            result.getInt("season")));
                } catch (JsonProcessingException exception) {
                    exception.printStackTrace();
                }
            }
            return null;
        });
        return list;
    }

    /**
     * Add.
     *
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param taskJson          the task json
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     * @param completedTime     the completed time
     * @param isClaimed         the is claimed
     * @param sprint            the sprint
     * @param season            the season
     */
    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String taskJson, String townName, String startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    missionType + "', '" +
                    addedTime + "', '" +
                    startedTime + "', '" +
                    allowedTime + "', '" +
                    taskJson + "', '" +
                    townName + "', '" +
                    startedPlayerUUID + "', '" +
                    completedTime + "', '" +
                    (isClaimed ? 1 : 0) + "', '" +
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
     * @param id                the id
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param taskJson          the task json
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     * @param completedTime     the completed time
     * @param isClaimed         the is claimed
     * @param sprint            the sprint
     * @param season            the season
     */
    public void update(int id, String missionType, long addedTime, long startedTime, long allowedTime, String taskJson, String townName, String startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET task_type='" + missionType +
                    "', added_time='" + addedTime +
                    "', started_time='" + startedTime +
                    "', allowed_time='" + allowedTime +
                    "', task_json='" + taskJson +
                    "', town='" + townName +
                    "', started_player='" + startedPlayerUUID +
                    "', completed_time='" + completedTime +
                    "', claimed='" + (isClaimed ? 1 : 0) +
                    "', sprint='" + sprint +
                    "', season='" + season +
                    "' WHERE id='" + id + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    public static MissionHistoryDatabase getInstance() {
        return singleton;
    }
}
