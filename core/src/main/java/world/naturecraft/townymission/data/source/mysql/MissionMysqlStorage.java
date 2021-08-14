package world.naturecraft.townymission.data.source.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.naturelib.database.MysqlStorage;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.data.storage.MissionStorage;
import world.naturecraft.townymission.utils.Util;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * The type Task database.
 */
public class MissionMysqlStorage extends MysqlStorage<MissionEntry> implements MissionStorage {

    /**
     * Instantiates a new Task database.
     *
     * @param db the db
     */
    public MissionMysqlStorage(HikariDataSource db) {
        super(
                db,
                Util.getDbName(DbType.MISSION, StorageType.MYSQL),
                TownyMissionInstance.getInstance().getInstanceConfig().getBoolean("database.mem-cache"));
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
                    "`num_mission` INT NOT NULL, " +
                    "PRIMARY KEY (`uuid`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();

            // 1. Adding num_mission column if it does not exists
            // 2. Assigning current missions with a number
            addColumn("num_mission", "INT", "0");
            assignNumber();

            return null;
        });
    }

    @Override
    public List<MissionEntry> getEntries() {
        if (cached) {
            List<MissionEntry> list = new ArrayList<>(memCache.values());
            return list;
        }

        List<MissionEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            try {
                ResultSet result = p.executeQuery();

                while (result.next()) {
                    list.add(new MissionEntry(UUID.fromString(result.getString("uuid")),
                            result.getString("task_type"),
                            result.getLong("added_time"),
                            result.getLong("started_time"),
                            result.getLong("allowed_time"),
                            result.getString("mission_json"),
                            UUID.fromString(result.getString("town_uuid")),
                            result.getString("started_player_uuid") == null || result.getString("started_player_uuid").equals("null") ? null : UUID.fromString(result.getString("started_player_uuid")),
                            result.getInt("num_mission")));
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
    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID, int numMission) {
        if (cached) {
            MissionEntry missionEntry = new MissionEntry(
                    UUID.randomUUID(), missionType, addedTime, startedTime, allowedTime, missionJson, townUUID, startedPlayerUUID, numMission);
            memCache.put(missionEntry.getId(), missionEntry);
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    addRemote(missionType, addedTime, startedTime, allowedTime, missionJson, townUUID, startedPlayerUUID, numMission);
                }
            };
            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        addRemote(missionType, addedTime, startedTime, allowedTime, missionJson, townUUID, startedPlayerUUID, numMission);
    }

    private void addRemote(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID, int numMission) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    missionType + "', '" +
                    addedTime + "', '" +
                    startedTime + "', '" +
                    allowedTime + "', '" +
                    missionJson + "', '" +
                    townUUID + "', '" +
                    startedPlayerUUID + "', '" +
                    numMission + "');";
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
    public void update(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID, int numMission) {
        if (cached) {
            MissionEntry missionEntry = new MissionEntry(
                    id, missionType, addedTime, startedTime, allowedTime, missionJson, townUUID, startedPlayerUUID, numMission);
            memCache.put(missionEntry.getId(), missionEntry);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    updateRemote(id, missionType, addedTime, startedTime, allowedTime, missionJson, townUUID, startedPlayerUUID, numMission);
                }
            };
            r.runTaskAsynchronously(TownyMissionInstance.getInstance());

            return;
        }

        updateRemote(id, missionType, addedTime, startedTime, allowedTime, missionJson, townUUID, startedPlayerUUID, numMission);
    }

    private void updateRemote(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID, int numMission) {
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

    public void update(MissionEntry data) {
        update(data.getId(), data.getMissionType().name(), data.getAddedTime(), data.getStartedTime(), data.getAllowedTime(), data.getMissionJson().toJson(), data.getTownUUID(), data.getStartedPlayerUUID(), data.getNumMission());
    }

    /**
     * This method is called by MissionStorage when num_mission column is added
     */
    public void assignNumber() {
        HashMap<UUID, List<MissionEntry>> townMap = new HashMap<>();
        for (MissionEntry e : getEntries()) {
            if (townMap.containsKey(e.getTownUUID())) {
                e.setNumMission(townMap.get(e.getTownUUID()).size());
                update(e);
                townMap.get(e.getTownUUID()).add(e);
            } else {
                List<MissionEntry> newList = new LinkedList<>();
                newList.add(e);
                townMap.put(e.getTownUUID(), newList);
            }
        }
    }
}
