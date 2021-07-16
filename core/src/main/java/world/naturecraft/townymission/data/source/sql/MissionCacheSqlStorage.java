package world.naturecraft.townymission.data.source.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.storage.MissionCacheStorage;
import world.naturecraft.townymission.services.StorageService;
import world.naturecraft.townymission.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MissionCacheSqlStorage extends SqlStorage<MissionCacheEntry> implements MissionCacheStorage {

    private static MissionCacheSqlStorage singleton;

    /**
     * Instantiates a new Database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public MissionCacheSqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
    }

    public static MissionCacheSqlStorage getInstance() {
        if (singleton == null) {
            singleton = new MissionCacheSqlStorage(StorageService.getInstance().getDataSource(), Util.getDbName(DbType.MISSION_CACHE));
        }

        return singleton;
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`uuid` VARCHAR(255) NOT NULL ," +
                    "`player_uuid` VARCHAR(255) NOT NULL ," +
                    "`mission_type` VARCHAR(255) NOT NULL ," +
                    "`amount` INT NOT NULL, " +
                    "PRIMARY KEY (`uuid`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
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
                            result.getInt("amount")));
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
    public void add(UUID playerUUID, MissionType missionType, int amount) {
        if (cached) {
            MissionCacheEntry missionCacheEntry = new MissionCacheEntry(UUID.randomUUID(), playerUUID, missionType, amount);
            memCache.put(missionCacheEntry.getId(), missionCacheEntry);
            return;
        }

        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    playerUUID.toString() + "', '" +
                    missionType.name() + "', '" +
                    amount + "');";
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


    @Override
    public void update(UUID uuid, UUID playerUUID, MissionType missionType, int amount) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET player_uuid='" + playerUUID.toString() +
                    "', mission_type='" + missionType.name() +
                    "', amount='" + amount +
                    "' WHERE uuid='" + uuid + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
