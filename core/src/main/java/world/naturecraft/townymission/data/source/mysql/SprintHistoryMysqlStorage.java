package world.naturecraft.townymission.data.source.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.naturelib.database.MysqlStorage;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.SprintHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.SprintHistoryStorage;
import world.naturecraft.townymission.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Sprint history database.
 */
public class SprintHistoryMysqlStorage extends MysqlStorage<SprintHistoryEntry> implements SprintHistoryStorage {

    /**
     * Instantiates a new Sprint history database.
     *
     * @param db the db
     */
    public SprintHistoryMysqlStorage(HikariDataSource db) {
        super(
                db,
                Util.getDbName(DbType.SPRINT_HISTORY, StorageType.MYSQL),
                TownyMissionInstance.getInstance().getInstanceConfig().getBoolean("database.mem-cache"));
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
        if (cached) {
            List<SprintHistoryEntry> list = new ArrayList<>(memCache.values());
            return list;
        }

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
        if (cached) {
            SprintHistoryEntry sprintHistoryEntry = new SprintHistoryEntry(UUID.randomUUID(), season, sprint, startedTime, rankJson);
            memCache.put(sprintHistoryEntry.getId(), sprintHistoryEntry);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    addRemote(season, sprint, startedTime, rankJson);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        addRemote(season, sprint, startedTime, rankJson);
    }

    public void addRemote(int season, int sprint, long startedTime, String rankJson) {
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
     * Update.
     *
     * @param uuid        the id
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(UUID uuid, int season, int sprint, long startedTime, String rankJson) {
        if (cached) {
            SprintHistoryEntry sprintHistoryEntry = new SprintHistoryEntry(uuid, season, sprint, startedTime, rankJson);
            memCache.put(sprintHistoryEntry.getId(), sprintHistoryEntry);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    updateRemote(uuid, season, sprint, startedTime, rankJson);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        updateRemote(uuid, season, sprint, startedTime, rankJson);
    }

    private void updateRemote(UUID uuid, int season, int sprint, long startedTime, String rankJson) {
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

    public void update(SprintHistoryEntry data) {
        update(data.getId(), data.getSeason(), data.getSprint(), data.getStartedTime(), data.getRankJson());
    }
}
