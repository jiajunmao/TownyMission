package world.naturecraft.townymission.core.data.yaml;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.entity.ClaimEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.data.db.ClaimStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Claim yaml storage.
 */
public class ClaimYamlStorage extends YamlStorage<ClaimEntry> implements ClaimStorage {

    private static ClaimYamlStorage singleton;

    /**
     * Instantiates a new Yaml storage.
     *
     * @param instance the instance
     * @param dbType   the db type
     */
    public ClaimYamlStorage(TownyMission instance, DbType dbType) {
        super(instance, dbType);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ClaimYamlStorage getInstance() {
        if (singleton == null) {
            TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            singleton = new ClaimYamlStorage(instance, DbType.CLAIM);
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
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".playerUUID", playerUUID);
        add(uuid + ".rewardType", rewardType);
        add(uuid + ".rewardJson", rewardJson);
        add(uuid + ".season", season);
        add(uuid + ".sprint", sprint);
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
        set(uuid + ".playerUUID", playerUUID);
        set(uuid + ".rewardType", rewardType);
        set(uuid + ".rewardJson", rewardJson);
        set(uuid + ".season", season);
        set(uuid + ".sprint", sprint);
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    @Override
    public List<ClaimEntry> getEntries() {
        List<ClaimEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new ClaimEntry(
                    key,
                    file.getString(key + ".playerUUID"),
                    file.getString(key + ".rewardType"),
                    file.getString(key + ".rewardJson"),
                    file.getInt(key + ".season"),
                    file.getInt(key + ".sprint")
            ));
        }

        return entryList;
    }
}
