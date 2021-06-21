package world.naturecraft.townymission.core.data.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.core.components.entity.SeasonEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.data.db.SeasonStorage;
import world.naturecraft.townymission.core.services.StorageService;
import world.naturecraft.townymission.core.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Season database.
 */
public class SeasonSqlStorage extends SqlStorage<SeasonEntry> implements SeasonStorage {

    private static SeasonSqlStorage singleton;

    /**
     * Instantiates a new Season database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public SeasonSqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonSqlStorage getInstance() {
        if (singleton == null) {
            singleton = new SeasonSqlStorage(
                    StorageService.getInstance().getDataSource(),
                    Util.getDbName(DbType.SEASON));
        }
        return singleton;
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`uuid` VARCHAR(255) NOT NULL ," +
                    "`town_id` VARCHAR(255) NOT NULL ," +
                    "`seasonpoints` INT NOT NULL, " +
                    "`season` INT NOT NULL ," +
                    "PRIMARY KEY (`uuid`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<SeasonEntry> getEntries() {
        List<SeasonEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            try {
                ResultSet result = p.executeQuery();
                while (result.next()) {
                    list.add(new SeasonEntry(
                            UUID.fromString(result.getString("uuid")),
                            UUID.fromString(result.getString("town_id")),
                            result.getInt("seasonpoints"),
                            result.getInt("season")));
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
        return list;
    }

    /**
     * Add.
     *
     * @param townUUID    the town uuid
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void add(UUID townUUID, int seasonPoint, int season) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    townUUID + "', '" +
                    seasonPoint + "', '" +
                    season + "');";
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
     * @param townUUID    the town uuid
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void update(UUID uuid, UUID townUUID, int seasonPoint, int season) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET town_id='" + townUUID +
                    "', seasonpoints='" + seasonPoint +
                    "', season='" + season +
                    "' WHERE uuid='" + uuid + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
