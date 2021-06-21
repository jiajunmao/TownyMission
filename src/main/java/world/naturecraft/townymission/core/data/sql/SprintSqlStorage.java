package world.naturecraft.townymission.core.data.sql;

import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.core.components.entity.SprintEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.data.db.SprintStorage;
import world.naturecraft.townymission.core.services.StorageService;
import world.naturecraft.townymission.core.utils.Util;

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
     * @param uuid         the id
     * @param townUUID     the town uuid
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    public void update(UUID uuid, UUID townUUID, int naturePoints, int sprint, int season) {
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
