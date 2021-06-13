package world.naturecraft.townymission.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.entity.ClaimEntry;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.data.sql.ClaimSqlStorage;
import world.naturecraft.townymission.data.sql.CooldownSqlStorage;
import world.naturecraft.townymission.data.yaml.ClaimYamlStorage;
import world.naturecraft.townymission.data.yaml.CooldownYamlStorage;

import java.util.List;
import java.util.UUID;

public class ClaimStorage extends Storage<ClaimEntry> {
    private static ClaimStorage singleton;
    private final StorageType storageType;

    public ClaimStorage(TownyMission instance) {
        storageType = instance.getStorageType();
    }

    public static ClaimStorage getInstance() {
        if (singleton == null) {
            TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            singleton = new ClaimStorage(instance);
        }

        return singleton;
    }

    public void add(String playerUUID, String rewardJson, int season, int sprint) {
        switch (storageType) {
            case YAML:
                ClaimYamlStorage.getInstance().add(playerUUID, rewardJson, season, sprint);
                break;
            case MYSQL:
                ClaimSqlStorage.getInstance().add(playerUUID, rewardJson, season, sprint);
                break;
        }
    }

    public void update(UUID uuid, String playerUUID, String rewardJson, int season, int sprint) {
        switch (storageType) {
            case YAML:
                ClaimYamlStorage.getInstance().update(uuid, playerUUID, rewardJson, season, sprint);
                break;
            case MYSQL:
                ClaimSqlStorage.getInstance().update(uuid, playerUUID, rewardJson, season, sprint);
                break;
        }
    }

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
