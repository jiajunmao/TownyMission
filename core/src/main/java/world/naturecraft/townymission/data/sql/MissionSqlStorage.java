package world.naturecraft.townymission.data.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.db.MissionStorage;
import world.naturecraft.townymission.services.StorageService;
import world.naturecraft.townymission.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Task database.
 */
public class MissionSqlStorage extends SqlStorage<MissionEntry> implements MissionStorage {

    private static MissionSqlStorage singleton;

    /**
     * Instantiates a new Task database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public MissionSqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionSqlStorage getInstance() {
        if (singleton == null) {
            singleton = new MissionSqlStorage(
                    StorageService.getInstance().getDataSource(),
                    Util.getDbName(DbType.MISSION));
        }
        return singleton;
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`uuid` VARCHAR(255) NOT NULL ," +
                    "`task_type` VARCHAR(255) NOT NULL ," +
                    "`added_time` BIGINT NOT NULL, " +
                    "`started_time` BIGINT NOT NULL, " +
                    "`allowed_time` BIGINT NOT NULL, " +
                    "`mission_json` VARCHAR(255) NOT NULL ," +
                    "`town_uuid` VARCHAR(255) NOT NULL ," +
                    "`started_player_uuid` VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (`uuid`))";
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
                        list.add(new MissionEntry(UUID.fromString(result.getString("uuid")),
                                result.getString("task_type"),
                                result.getLong("added_time"),
                                result.getLong("started_time"),
                                result.getLong("allowed_time"),
                                result.getString("mission_json"),
                                UUID.fromString(result.getString("town_uuid")),
                                UUID.fromString(result.getString("started_player_uuid"))));
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
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player uuid
     */
    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    missionType + "', '" +
                    addedTime + "', '" +
                    startedTime + "', '" +
                    allowedTime + "', '" +
                    missionJson + "', '" +
                    townUUID + "', '" +
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
                    "uuid='" + id + "');";
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
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player uuid
     */
    public void update(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET task_type='" + missionType +
                    "', added_time='" + addedTime +
                    "', started_time='" + startedTime +
                    "', allowed_time='" + allowedTime +
                    "', mission_json='" + missionJson +
                    "', town_uuid='" + townUUID +
                    "', started_player_uuid='" + startedPlayerUUID +
                    "' WHERE uuid='" + id + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
