package world.naturecraft.townymission.data.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.components.entity.MissionEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Task database.
 */
public class MissionDatabase extends Database<MissionEntry> {

    private static MissionDatabase singleton = null;

    /**
     * Instantiates a new Task database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public MissionDatabase(HikariDataSource db, String tableName) {
        super(db, tableName);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionDatabase getInstance() {
        return singleton;
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` VARCHAR(255) NOT NULL ," +
                    "`task_type` VARCHAR(255) NOT NULL ," +
                    "`added_time` BIGINT NOT NULL, " +
                    "`started_time` BIGINT NOT NULL, " +
                    "`allowed_time` BIGINT NOT NULL, " +
                    "`task_json` VARCHAR(255) NOT NULL ," +
                    "`town` VARCHAR(255) NOT NULL ," +
                    "`started_player` VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<MissionEntry> getEntries() {
        List<MissionEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            try {
                ResultSet result = p.executeQuery();

                while (result.next()) {
                    try {
                        list.add(new MissionEntry(UUID.fromString(result.getString("id")),
                                result.getString("task_type"),
                                result.getLong("added_time"),
                                result.getLong("started_time"),
                                result.getLong("allowed_time"),
                                result.getString("task_json"),
                                result.getString("town"),
                                result.getString("started_player")));
                    } catch (JsonProcessingException exception) {
                        exception.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
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
     * @param missionJson       the mission json
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     */
    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    missionType + "', '" +
                    addedTime + "', '" +
                    startedTime + "', '" +
                    allowedTime + "', '" +
                    missionJson + "', '" +
                    townName + "', '" +
                    startedPlayerUUID + "');";
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
     * @param id                the id
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the mission json
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     */
    public void update(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET task_type='" + missionType +
                    "', added_time='" + addedTime +
                    "', started_time='" + startedTime +
                    "', allowed_time='" + allowedTime +
                    "', task_json='" + missionJson +
                    "', town='" + townName +
                    "', started_player='" + startedPlayerUUID +
                    "' WHERE id='" + id + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
