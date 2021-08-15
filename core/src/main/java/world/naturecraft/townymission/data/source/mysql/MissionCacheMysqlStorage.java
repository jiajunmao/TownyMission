package world.naturecraft.townymission.data.source.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.naturelib.database.MysqlStorage;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.storage.MissionCacheStorage;
import world.naturecraft.townymission.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MissionCacheMysqlStorage extends MysqlStorage<MissionCacheEntry> implements MissionCacheStorage {

    /**
     * Instantiates a new Database.
     *
     * @param db the db
     */
    public MissionCacheMysqlStorage(HikariDataSource db) {
        super(
                db,
                Util.getDbName(DbType.MISSION_CACHE, StorageType.MYSQL),
                TownyMissionInstance.getInstance().getInstanceConfig().getBoolean("database.mem-cache"));
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`uuid` VARCHAR(255) NOT NULL ," +
                    "`player_uuid` VARCHAR(255) NOT NULL ," +
                    "`mission_type` VARCHAR(255) NOT NULL ," +
                    "`amount` INT NOT NULL, " +
                    "`last_attempted` BIGINT NOT NULL, " +
                    "`retry_count` INT NOT NULL, " +
                    "PRIMARY KEY (`uuid`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();

            addColumn("last_attempted", "BIGINT", String.valueOf(new Date().getTime()));
            addColumn("retry_count", "INT", "0");

            return null;
        });
    }

    @Override
    public List<MissionCacheEntry> getEntries() {
        if (cached) {
            List<MissionCacheEntry> list = new ArrayList<>(memCache.values());
            return list;
        }

        List<MissionCacheEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            try {
                ResultSet result = p.executeQuery();

                while (result.next()) {
                    list.add(new MissionCacheEntry(
                            UUID.fromString(result.getString("uuid")),
                            UUID.fromString(result.getString("player_uuid")),
                            MissionType.valueOf(result.getString("mission_type").toUpperCase(Locale.ROOT)),
                            result.getInt("amount"),
                            result.getLong("last_attempted"),
                            result.getInt("retry_count")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        });
        return list;
    }

    @Override
    public void add(UUID playerUUID, MissionType missionType, int amount, long lastAttempted, int retryCount) {
        if (cached) {
            MissionCacheEntry missionCacheEntry = new MissionCacheEntry(UUID.randomUUID(), playerUUID, missionType, amount, lastAttempted, retryCount);
            memCache.put(missionCacheEntry.getId(), missionCacheEntry);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    addRemote(playerUUID, missionType, amount, lastAttempted, retryCount);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        addRemote(playerUUID, missionType, amount, lastAttempted, retryCount);
    }

    private void addRemote(UUID playerUUID, MissionType missionType, int amount, long lastAttempted, int retryCount) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid.toString() + "', '" +
                    playerUUID.toString() + "', '" +
                    missionType.name() + "', '" +
                    amount + "', '" +
                    lastAttempted + "', '" +
                    retryCount + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public void update(UUID uuid, UUID playerUUID, MissionType missionType, int amount, long lastAttempted, int retryCount) {
        if (cached) {
            MissionCacheEntry missionCacheEntry = new MissionCacheEntry(uuid, playerUUID, missionType, amount, lastAttempted, retryCount);
            memCache.put(missionCacheEntry.getId(), missionCacheEntry);
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    updateRemote(uuid, playerUUID, missionType, amount, lastAttempted, retryCount);
                }
            };
            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        updateRemote(uuid, playerUUID, missionType, amount, lastAttempted, retryCount);
    }

    private void updateRemote(UUID uuid, UUID playerUUID, MissionType missionType, int amount, long lastAttempted, int retryCount) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET player_uuid='" + playerUUID.toString() +
                    "', mission_type='" + missionType.name() +
                    "', amount='" + amount +
                    "', lastAttempted='" + lastAttempted +
                    "', retryCount='" + retryCount +
                    "' WHERE uuid='" + uuid.toString() + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    public void update(MissionCacheEntry data) {
        update(data.getId(), data.getPlayerUUID(), data.getMissionType(), data.getAmount(), data.getLastAttempted(), data.getRetryCount());
    }
}
