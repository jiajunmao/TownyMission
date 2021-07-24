package world.naturecraft.townymission.data.source.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.data.storage.SeasonHistoryStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Season history database.
 */
public class SeasonHistorySqlStorage extends SqlStorage<SeasonHistoryEntry> implements SeasonHistoryStorage {

    /**
     * Instantiates a new Season history database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public SeasonHistorySqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
    }

    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`uuid` VARCHAR(255) NOT NULL ," +
                    "`season` INT NOT NULL ," +
                    "`started_time` BIGINT NOT NULL ," +
                    "`rank_json` VARCHAR(255) NOT NULL ," +
                    "PRIMARY KEY (`uuid`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    @Override
    public List<SeasonHistoryEntry> getEntries() {
        if (cached) {
            List<SeasonHistoryEntry> list = new ArrayList<>(memCache.values());
            return list;
        }

        List<SeasonHistoryEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName;
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();

            while (result.next()) {
                list.add(new SeasonHistoryEntry(UUID.fromString(result.getString("uuid")),
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
        if (cached) {
            SeasonHistoryEntry seasonHistoryEntry = new SeasonHistoryEntry(UUID.randomUUID(), season, startedTime, rankJson);
            memCache.put(seasonHistoryEntry.getId(), seasonHistoryEntry);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    addRemote(season, startedTime, rankJson);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        addRemote(season, startedTime, rankJson);
    }

    private void addRemote(int season, long startedTime, String rankJson) {
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
     * Update.
     *
     * @param uuid        the id
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(UUID uuid, int season, long startedTime, String rankJson) {
        if (cached) {
            SeasonHistoryEntry seasonHistoryEntry = new SeasonHistoryEntry(uuid, season, startedTime, rankJson);
            memCache.put(seasonHistoryEntry.getId(), seasonHistoryEntry);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    updateRemote(uuid, season, startedTime, rankJson);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        updateRemote(uuid, season, startedTime, rankJson);
    }

    private void updateRemote(UUID uuid, int season, long startedTime, String rankJson) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET season='" + season +
                    "', started_time='" + startedTime +
                    "', rank_json='" + rankJson +
                    "' WHERE uuid='" + uuid + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }


    public void update(SeasonHistoryEntry data) {
        update(data.getId(), data.getSeason(), data.getStartTime(), data.getRankJson());
    }
}
