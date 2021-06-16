package world.naturecraft.townymission.core.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.entity.ClaimEntry;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.data.sql.ClaimSqlStorage;
import world.naturecraft.townymission.core.data.yaml.ClaimYamlStorage;

import java.util.List;
import java.util.UUID;

/**
 * The type Claim storage.
 */
public class ClaimStorage extends Storage<ClaimEntry> {
    private static ClaimStorage singleton;
    private final StorageType storageType;

    /**
     * Instantiates a new Claim storage.
     *
     * @param instance the instance
     */
    public ClaimStorage(TownyMission instance) {
        storageType = instance.getStorageType();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ClaimStorage getInstance() {
        if (singleton == null) {
            TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            singleton = new ClaimStorage(instance);
        }

        return singleton;
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
        switch (storageType) {
            case YAML:
                ClaimYamlStorage.getInstance().add(playerUUID, rewardType, rewardJson, season, sprint);
                break;
            case MYSQL:
                ClaimSqlStorage.getInstance().add(playerUUID, rewardType, rewardJson, season, sprint);
                break;
        }
    }

    /**
     * Update.
     *
     * @param uuid       the uuid
     * @param playerUUID the player uuid
     * @param rewardJson the reward json
     * @param season     the season
     * @param sprint     the sprint
     */
    public void update(UUID uuid, String playerUUID, String rewardType, String rewardJson, int season, int sprint) {
        switch (storageType) {
            case YAML:
                ClaimYamlStorage.getInstance().update(uuid, playerUUID, rewardType, rewardJson, season, sprint);
                break;
            case MYSQL:
                ClaimSqlStorage.getInstance().update(uuid, playerUUID, rewardType, rewardJson, season, sprint);
                break;
        }
    }

    /**
     * Remove.
     *
     * @param id the id
     */
    public void remove(UUID id) {
        switch (storageType) {
            case YAML:
                ClaimYamlStorage.getInstance().remove(id);
                break;
            case MYSQL:
                ClaimSqlStorage.getInstance().remove(id);
                break;
        }
    }

    public List<ClaimEntry> getEntries() {
        switch (storageType) {
            case YAML:
                return ClaimYamlStorage.getInstance().getEntries();
            case MYSQL:
                return ClaimSqlStorage.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }
}
