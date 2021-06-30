package world.naturecraft.townymission.core.data.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.core.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.core.components.entity.MissionEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.MissionJson;
import world.naturecraft.townymission.core.data.db.MissionCacheStorage;
import world.naturecraft.townymission.core.services.StorageService;
import world.naturecraft.townymission.core.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MissionCacheSqlStorage extends SqlStorage<MissionCacheEntry> implements MissionCacheStorage {

    private MissionCacheSqlStorage singleton;

    public MissionCacheSqlStorage getInstance() {
        if (singleton == null) {
            singleton = new MissionCacheSqlStorage(StorageService.getInstance().getDataSource(), Util.getDbName(DbType.MISSION_CACHE));
        }

        return singleton;
    }
    /**
     * Instantiates a new Database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public MissionCacheSqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
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
    public void remove(UUID id) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "uuid='" + id + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

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
