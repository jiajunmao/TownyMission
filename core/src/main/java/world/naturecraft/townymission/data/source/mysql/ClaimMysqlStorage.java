package world.naturecraft.townymission.data.source.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.ClaimEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.ClaimStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Claim sql storage.
 */
public class ClaimMysqlStorage extends MysqlStorage<ClaimEntry> implements ClaimStorage {

    /**
     * Instantiates a new Database.
     *
     * @param db the db
     */
    public ClaimMysqlStorage(HikariDataSource db) {
        super(db, DbType.CLAIM);
    }

    /**
     * Create table.
     */
    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`uuid` VARCHAR(255) NOT NULL," +
                    "`player_uuid` VARCHAR(255) NOT NULL ," +
                    "`reward_type` VARCHAR(255) NOT NULL ," +
                    "`reward_json` VARCHAR(255) NOT NULL," +
                    "`sprint` INT NOT NULL," +
                    "`season` INT NOT NULL," +
                    "PRIMARY KEY (`uuid`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    @Override
    public List<ClaimEntry> getEntries() {
        if (cached) {
            List<ClaimEntry> list = new ArrayList<>(memCache.values());
            return list;
        }

        List<ClaimEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                list.add(new ClaimEntry(
                        UUID.fromString(result.getString("uuid")),
                        UUID.fromString(result.getString("player_uuid")),
                        result.getString("reward_type"),
                        result.getString("reward_json"),
                        result.getInt("season"),
                        result.getInt("sprint")
                ));
            }
            return null;
        });
        return list;
    }

    /**
     * Add.
     *
     * @param playerUUID the player uuid
     * @param rewardJson the reward json
     * @param season     the season
     * @param sprint     the sprint
     */
    public void add(UUID playerUUID, String rewardType, String rewardJson, int season, int sprint) {
        if (cached) {
            ClaimEntry claimEntry = new ClaimEntry(
                    UUID.randomUUID(), playerUUID, rewardType, rewardJson, season, sprint
            );
            memCache.put(claimEntry.getId(), claimEntry);
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    addRemote(playerUUID, rewardType, rewardJson, season, sprint);
                }
            };
            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        addRemote(playerUUID, rewardType, rewardJson, season, sprint);
    }

    private void addRemote(UUID playerUUID, String rewardType, String rewardJson, int season, int sprint) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    playerUUID + "', '" +
                    rewardType + "', '" +
                    rewardJson + "', '" +
                    season + "', '" +
                    sprint + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Update.
     *
     * @param uuid       the id
     * @param playerUUID the player uuid
     * @param rewardJson the reward json
     * @param season     the season
     * @param sprint     the sprint
     */
    public void update(UUID uuid, UUID playerUUID, String rewardType, String rewardJson, int season, int sprint) {
        if (cached) {
            ClaimEntry claimEntry = new ClaimEntry(
                    uuid, playerUUID, rewardType, rewardJson, season, sprint
            );
            memCache.remove(uuid);
            memCache.put(claimEntry.getId(), claimEntry);

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    updateRemote(uuid, playerUUID, rewardType, rewardJson, season, sprint);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        updateRemote(uuid, playerUUID, rewardType, rewardJson, season, sprint);

    }

    private void updateRemote(UUID uuid, UUID playerUUID, String rewardType, String rewardJson, int season, int sprint) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET player_uuid='" + playerUUID +
                    "', reward_type='" + rewardType +
                    "', reward_json='" + rewardJson +
                    "', season='" + season +
                    "', sprint='" + sprint +
                    "' WHERE uuid='" + uuid + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    public void update(ClaimEntry data) {
        update(data.getId(), data.getPlayerUUID(), data.getRewardType().name(), data.getRewardJson().toJson(), data.getSeason(), data.getSprint());
    }
}
