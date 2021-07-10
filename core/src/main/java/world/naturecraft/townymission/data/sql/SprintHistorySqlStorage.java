package world.naturecraft.townymission.data.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.components.entity.SprintHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.db.SprintHistoryStorage;
import world.naturecraft.townymission.services.StorageService;
import world.naturecraft.townymission.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Sprint history database.
 */
public class SprintHistorySqlStorage extends SqlStorage<SprintHistoryEntry> implements SprintHistoryStorage {

    private static SprintHistorySqlStorage singleton;

    /**
     * Instantiates a new Sprint history database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public SprintHistorySqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintHistorySqlStorage getInstance() {
        if (singleton == null) {
            singleton = new SprintHistorySqlStorage(
                    StorageService.getInstance().getDataSource(),
                    Util.getDbName(DbType.SPRINT_HISTORY));
        }
        return singleton;
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`uuid` VARCHAR(255) NOT NULL ," +
                    "`season` INT NOT NULL ," +
                    "`sprint` INT NOT NULL, " +
                    "`started_time` BIGINT NOT NULL, " +
                    "`rank_json` VARCHAR(255) NOT NULL ," +
                    "PRIMARY KEY (`uuid`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<SprintHistoryEntry> getEntries() {
        List<SprintHistoryEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName;
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();

            while (result.next()) {
                list.add(new SprintHistoryEntry(UUID.fromString(result.getString("uuid")),
                        result.getInt("season"),
                        result.getInt("sprint"),
                        result.getLong("started_time"),
                        result.getString("rank_json")));
            }

            return null;
        });
        return list;
    }

    /**
     * Add.
     *
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void add(int season, int sprint, long startedTime, String rankJson) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    season + "' , '" +
                    sprint + "' , '" +
                    startedTime + "' , '" +
                    rankJson + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Remove.
     *
     * @param uuid the id
     */
    public void remove(UUID uuid) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "uuid='" + uuid + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Update.
     *
     * @param uuid        the id
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(UUID uuid, int season, int sprint, long startedTime, String rankJson) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET season='" + season +
                    "', sprint='" + sprint +
                    "', started_time='" + startedTime +
                    "', rank_json='" + rankJson +
                    "' WHERE uuid='" + uuid + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
