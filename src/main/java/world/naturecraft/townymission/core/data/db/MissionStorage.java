/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.entity.MissionEntry;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.data.sql.MissionSqlStorage;
import world.naturecraft.townymission.core.data.yaml.MissionYamlStorage;

import java.util.List;
import java.util.UUID;

/**
 * The type Mission storage.
 */
public class MissionStorage extends Storage<MissionEntry> {

    private static MissionStorage singleton;
    private final StorageType storageType;

    /**
     * Instantiates a new Mission storage.
     *
     * @param instance the instance
     */
    public MissionStorage(TownyMission instance) {
        storageType = instance.getStorageType();
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionStorage getInstance() {
        if (singleton == null) {
            new MissionStorage((TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission"));
        }

        return singleton;
    }

    /**
     * Add.
     *
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the mission json
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     */
    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID) {
        switch (storageType) {
            case YAML:
                MissionYamlStorage.getInstance().add(missionType, addedTime, startedTime, allowedTime, missionJson, townName, startedPlayerUUID);
                break;
            case MYSQL:
                MissionSqlStorage.getInstance().add(missionType, addedTime, startedTime, allowedTime, missionJson, townName, startedPlayerUUID);
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
                MissionYamlStorage.getInstance().remove(id);
                break;
            case MYSQL:
                MissionSqlStorage.getInstance().remove(id);
                break;
        }
    }

    /**
     * Update.
     *
     * @param id                the id
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the mission json
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     */
    public void update(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID) {
        switch (storageType) {
            case YAML:
                MissionYamlStorage.getInstance().update(id, missionType, addedTime, startedTime, allowedTime, missionJson, townName, startedPlayerUUID);
                break;
            case MYSQL:
                MissionSqlStorage.getInstance().update(id, missionType, addedTime, startedTime, allowedTime, missionJson, townName, startedPlayerUUID);
                break;
        }
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<MissionEntry> getEntries() {
        switch (storageType) {
            case YAML:
                return MissionYamlStorage.getInstance().getEntries();
            case MYSQL:
                return MissionSqlStorage.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }
}
