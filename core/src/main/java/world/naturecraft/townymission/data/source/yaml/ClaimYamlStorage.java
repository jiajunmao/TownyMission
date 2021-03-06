package world.naturecraft.townymission.data.source.yaml;

import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.naturelib.database.YamlStorage;
import world.naturecraft.townymission.components.entity.ClaimEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.ClaimStorage;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Claim yaml storage.
 */
public class ClaimYamlStorage extends YamlStorage<ClaimEntry> implements ClaimStorage {


    /**
     * Instantiates a new Yaml storage.
     */
    public ClaimYamlStorage() {
        super(Util.getDbName(DbType.CLAIM, StorageType.YAML));
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
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".playerUUID", playerUUID.toString());
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
    public void update(UUID uuid, UUID playerUUID, String rewardType, String rewardJson, int season, int sprint) {
        set(uuid + ".playerUUID", playerUUID.toString());
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
                    UUID.fromString(key),
                    UUID.fromString(file.getString(key + ".playerUUID")),
                    file.getString(key + ".rewardType"),
                    file.getString(key + ".rewardJson"),
                    file.getInt(key + ".season"),
                    file.getInt(key + ".sprint")
            ));
        }

        return entryList;
    }
}
