package world.naturecraft.townymission.data.yaml;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.entity.ClaimEntry;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.json.reward.RewardJson;
import world.naturecraft.townymission.data.sql.SqlStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimYamlStorage extends YamlStorage<ClaimEntry> {

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

    public static ClaimYamlStorage getInstance() {
        if (singleton == null) {
            TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            singleton = new ClaimYamlStorage(instance, DbType.CLAIM);
        }

        return singleton;
    }

    public void add(String playerUUID, String rewardJson, int season, int sprint) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".playerUUID", playerUUID);
        add(uuid + ".rewardJson", rewardJson);
        add(uuid + ".season", season);
        add(uuid + ".sprint", sprint);
    }

    public void update(UUID uuid, String playerUUID, String rewardJson, int season, int sprint) {
        set(uuid + ".playerUUID", playerUUID);
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
                    file.getString(key + ".rewardJson"),
                    file.getInt(key + ".season"),
                    file.getInt(key + ".sprint")
            ));
        }

        return entryList;
    }
}
