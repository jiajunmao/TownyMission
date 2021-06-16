package world.naturecraft.townymission.core.data.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.entity.ClaimEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.data.db.ClaimStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Claim sql storage.
 */
public class ClaimSqlStorage extends SqlStorage<ClaimEntry> implements ClaimStorage {

    private static ClaimSqlStorage singleton;

    /**
     * Instantiates a new Database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public ClaimSqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ClaimSqlStorage getInstance() {
        if (singleton == null) {
            TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            singleton = new ClaimSqlStorage(instance.getDatasource(), BukkitUtil.getDbName(DbType.CLAIM));
        }

        return singleton;
    }

    /**
     * Create table.
     */
    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` VARCHAR(255) NOT NULL," +
                    "`player_uuid` VARCHAR(255) NOT NULL ," +
                    "`reward_type` VARCHAR(255) NOT NULL ," +
                    "`reward_json` VARCHAR(255) NOT NULL," +
                    "`sprint` INT NOT NULL," +
                    "`season` INT NOT NULL," +
                    "PRIMARY KEY (`id`))";
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
        List<ClaimEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                list.add(new ClaimEntry(
                        result.getString("id"),
                        result.getString("player_uuid"),
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
    public void add(String playerUUID, String rewardType, String rewardJson, int season, int sprint) {
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
     * Remove.
     *
     * @param id the id
     */
    public void remove(UUID id) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "id='" + id.toString() + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Update.
     *
     * @param id         the id
     * @param playerUUID the player uuid
     * @param rewardJson the reward json
     * @param season     the season
     * @param sprint     the sprint
     */
    public void update(UUID id, String playerUUID, String rewardType, String rewardJson, int season, int sprint) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET player_uuid='" + playerUUID +
                    "', rewardType='" + rewardType +
                    "', reward_json='" + rewardJson +
                    "', season='" + season +
                    "', sprint='" + sprint +
                    "' WHERE id='" + id.toString() + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
