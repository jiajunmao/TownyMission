package world.naturecraft.townymission.core.data.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.core.components.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.enums.ServerType;
import world.naturecraft.townymission.core.data.db.SeasonHistoryStorage;
import world.naturecraft.townymission.core.services.StorageService;
import world.naturecraft.townymission.core.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Season history database.
 */
public class SeasonHistorySqlStorage extends SqlStorage<SeasonHistoryEntry> implements SeasonHistoryStorage {

    private static SeasonHistorySqlStorage singleton;

    /**
     * Instantiates a new Season history database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public SeasonHistorySqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonHistorySqlStorage getInstance() {
        if (singleton == null) {
            singleton = new SeasonHistorySqlStorage(
                    StorageService.getInstance().getDataSource(),
                    Util.getDbName(DbType.SEASON_HISTORY));
        }
        return singleton;
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` VARCHAR(255) NOT NULL ," +
                    "`season` INT NOT NULL ," +
                    "`started_time` BIGINT NOT NULL ," +
                    "`rank_json` VARCHAR(255) NOT NULL ," +
                    "PRIMARY KEY (`id`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<SeasonHistoryEntry> getEntries() {
        List<SeasonHistoryEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName;
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();

            while (result.next()) {
                list.add(new SeasonHistoryEntry(UUID.fromString(result.getString("id")),
                        result.getInt("season"),
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
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void add(int season, long startedTime, String rankJson) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    season + "', '" +
                    startedTime + "', '" +
                    rankJson + "');";
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
     * @param id          the id
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(UUID id, int season, long startedTime, String rankJson) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET season='" + season +
                    "', started_time='" + startedTime +
                    "', rank_json='" + rankJson +
                    "' WHERE id='" + id + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
