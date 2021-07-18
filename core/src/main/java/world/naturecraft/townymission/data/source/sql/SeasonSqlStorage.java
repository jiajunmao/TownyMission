package world.naturecraft.townymission.data.source.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.SeasonEntry;
import world.naturecraft.townymission.components.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.SeasonStorage;
import world.naturecraft.townymission.services.StorageService;
import world.naturecraft.townymission.utils.Util;

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

    /**
     * Instantiates a new Season database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public SeasonSqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
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
        if (cached) {
            List<SeasonEntry> list = new ArrayList<>(memCache.values());
            return list;
        }
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
        if (cached) {
            SeasonEntry seasonEntry = new SeasonEntry(UUID.randomUUID(), townUUID, seasonPoint, season);
            memCache.put(seasonEntry.getId(), seasonEntry);
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    addRemote(townUUID, seasonPoint, season);
                }
            };
            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        addRemote(townUUID, seasonPoint, season);
    }

    private void addRemote(UUID townUUID, int seasonPoint, int season) {
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
     * Update.
     *
     * @param uuid        the id
     * @param townUUID    the town uuid
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void update(UUID uuid, UUID townUUID, int seasonPoint, int season) {
        if (cached) {
            SeasonEntry seasonEntry = new SeasonEntry(uuid, townUUID, seasonPoint, season);
            memCache.put(seasonEntry.getId(), seasonEntry);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    updateRemote(uuid, townUUID, seasonPoint, season);
                }
            };
            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        updateRemote(uuid, townUUID, seasonPoint, season);
    }

    private void updateRemote(UUID uuid, UUID townUUID, int seasonPoint, int season) {
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

    public void update(SeasonEntry data) {
        update(data.getId(), data.getTownUUID(), data.getSeasonPoint(), data.getSeason());
    }
}
