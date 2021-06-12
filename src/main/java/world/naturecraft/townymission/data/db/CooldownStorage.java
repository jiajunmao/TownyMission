/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.data.sql.CooldownDatabase;
import world.naturecraft.townymission.data.yaml.CooldownYaml;

import java.util.List;
import java.util.UUID;

/**
 * The type Cooldown storage.
 */
public class CooldownStorage extends Storage<CooldownEntry> {

    private static CooldownStorage singleton;
    private final StorageType storageType;

    /**
     * Instantiates a new Cooldown storage.
     *
     * @param instance the instance
     */
    public CooldownStorage(TownyMission instance) {
        storageType = instance.getStorageType();
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CooldownStorage getInstance() {
        if (singleton == null) {
            new CooldownStorage((TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission"));
        }

        return singleton;
    }

    /**
     * Add.
     *
     * @param townUUID    the town uuid
     * @param startedTime the started time
     * @param cooldown    the cooldown
     */
    public void add(String townUUID, long startedTime, long cooldown) {
        switch (storageType) {
            case YAML:
                CooldownYaml.getInstance().add(townUUID, startedTime, cooldown);
                break;
            case MYSQL:
                CooldownDatabase.getInstance().add(townUUID, startedTime, cooldown);
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
                CooldownYaml.getInstance().remove(id);
                break;
            case MYSQL:
                CooldownDatabase.getInstance().remove(id);
                break;
        }
    }

    /**
     * Update.
     *
     * @param id          the id
     * @param townUUID    the town uuid
     * @param startedTime the started time
     * @param cooldown    the cooldown
     */
    public void update(UUID id, String townUUID, long startedTime, long cooldown) {
        switch (storageType) {
            case YAML:
                CooldownYaml.getInstance().update(id, townUUID, startedTime, cooldown);
                break;
            case MYSQL:
                CooldownDatabase.getInstance().update(id, townUUID, startedTime, cooldown);
                break;
        }
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<CooldownEntry> getEntries() {
        switch (storageType) {
            case YAML:
                return CooldownYaml.getInstance().getEntries();
            case MYSQL:
                return CooldownDatabase.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }

}
