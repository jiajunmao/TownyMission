package world.naturecraft.townymission.data.source.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.components.entity.SprintEntry;
import world.naturecraft.townymission.components.entity.SprintHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.SprintStorage;
import world.naturecraft.townymission.services.StorageService;
import world.naturecraft.townymission.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Sprint database.
 */
public class SprintSqlStorage extends SqlStorage<SprintEntry> implements SprintStorage {

    private static SprintSqlStorage singleton;

    /**
     * Instantiates a new Sprint database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public SprintSqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintSqlStorage getInstance() {
        if (singleton == null) {
            singleton = new SprintSqlStorage(
                    StorageService.getInstance().getDataSource(),
                    Util.getDbName(DbType.SPRINT));
        }
        return singleton;
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`uuid` VARCHAR(255) NOT NULL ," +
                    "`town_id` VARCHAR(255) NOT NULL ," +
                    "`naturepoints` INT NOT NULL, " +
                    "`sprint` INT NOT NULL ," +
                    "`season` INT NOT NULL ," +
                    "PRIMARY KEY (`uuid`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<SprintEntry> getEntries() {
        if (cached) {
            List<SprintEntry> list = new ArrayList<>(memCache.values());
            return list;
        }
        List<SprintEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            try {
                ResultSet result = p.executeQuery();

                while (result.next()) {
                    SprintEntry entry = new SprintEntry(UUID.fromString(result.getString("uuid")),
                            UUID.fromString(result.getString("town_id")),
                            result.getInt("naturepoints"),
                            result.getInt("sprint"),
                            result.getInt("season"));
                    list.add(entry);
                }
            } catch (SQLException e) {
                return null;
            }

            return null;
        });
        return list;
    }

    /**
     * Add.
     *
     * @param townUUID     the town uuid
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    public void add(UUID townUUID, int naturePoints, int sprint, int season) {
        if (cached) {
            SprintEntry sprintEntry = new SprintEntry(UUID.randomUUID(), townUUID, naturePoints, sprint, season);
            memCache.put(sprintEntry.getId(), sprintEntry);
            return;
        }
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    townUUID + "', '" +
                    naturePoints + "', '" +
                    sprint + "', '" +
                    season + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Update.
     *
     * @param uuid         the id
     * @param townUUID     the town uuid
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    public void update(UUID uuid, UUID townUUID, int naturePoints, int sprint, int season) {
        if (cached) {
            SprintEntry sprintEntry = new SprintEntry(uuid, townUUID, naturePoints, sprint, season);
            memCache.put(sprintEntry.getId(), sprintEntry);
            return;
        }
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET town_id='" + townUUID +
                    "', naturepoints='" + naturePoints +
                    "', sprint='" + sprint +
                    "', season='" + season +
                    "' WHERE uuid='" + uuid + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
