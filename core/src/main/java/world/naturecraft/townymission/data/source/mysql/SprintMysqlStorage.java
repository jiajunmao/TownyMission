package world.naturecraft.townymission.data.source.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.naturelib.database.MysqlStorage;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.SprintEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.SprintStorage;
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
public class SprintMysqlStorage extends MysqlStorage<SprintEntry> implements SprintStorage {

    /**
     * Instantiates a new Sprint database.
     *
     * @param db the db
     */
    public SprintMysqlStorage(HikariDataSource db) {
        super(
                db,
                Util.getDbName(DbType.SPRINT, StorageType.MYSQL),
                TownyMissionInstance.getInstance().getInstanceConfig().getBoolean("database.mem-cache"));
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
        UUID randomUUID = UUID.randomUUID();
        if (cached) {
            SprintEntry sprintEntry = new SprintEntry(randomUUID, townUUID, naturePoints, sprint, season);
            memCache.put(sprintEntry.getId(), sprintEntry);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    addRemote(randomUUID, townUUID, naturePoints, sprint, season);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        addRemote(randomUUID, townUUID, naturePoints, sprint, season);
    }

    private void addRemote(UUID randomUUID, UUID townUUID, int naturePoints, int sprint, int season) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES('" + randomUUID + "', '" +
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

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    updateRemote(uuid, townUUID, naturePoints, sprint, season);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        updateRemote(uuid, townUUID, naturePoints, sprint, season);
    }

    private void updateRemote(UUID uuid, UUID townUUID, int naturePoints, int sprint, int season) {
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

    public void update(SprintEntry data) {
        update(data.getId(), data.getTownUUID(), data.getNaturepoints(), data.getSprint(), data.getSeason());
    }
}
