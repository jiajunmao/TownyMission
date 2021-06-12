/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.MissionHistoryEntry;
import world.naturecraft.townymission.components.containers.sql.MissionHistoryEntry;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.data.sql.MissionHistoryDatabase;
import world.naturecraft.townymission.data.yaml.MissionHistoryYaml;

import java.util.List;
import java.util.UUID;

/**
 * The type Mission history storage.
 */
public class MissionHistoryStorage extends Storage<MissionHistoryEntry> {
    
    private static MissionHistoryStorage singleton;
    private StorageType storageType;

    /**
     * Instantiates a new Mission history storage.
     *
     * @param instance the instance
     */
    public MissionHistoryStorage(TownyMission instance) {
        storageType = instance.getStorageType();
        singleton = this;
    }

    /**
     * Add.
     *
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param taskJson          the task json
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     * @param completedTime     the completed time
     * @param isClaimed         the is claimed
     * @param sprint            the sprint
     * @param season            the season
     */
    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String taskJson, String townName, String startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season) {
        switch (storageType) {
            case YAML:
                MissionHistoryYaml.getInstance().add(missionType, addedTime, startedTime, allowedTime, taskJson, townName, startedPlayerUUID, completedTime, isClaimed, sprint, season);
                break;
            case MYSQL:
                MissionHistoryDatabase.getInstance().add(missionType, addedTime, startedTime, allowedTime, taskJson, townName, startedPlayerUUID, completedTime, isClaimed, sprint, season);
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
                MissionHistoryYaml.getInstance().remove(id);
                break;
            case MYSQL:
                MissionHistoryDatabase.getInstance().remove(id);
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
     * @param taskJson          the task json
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     * @param completedTime     the completed time
     * @param isClaimed         the is claimed
     * @param sprint            the sprint
     * @param season            the season
     */
    public void update(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String taskJson, String townName, String startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season) {
        switch (storageType) {
            case YAML:
                MissionHistoryYaml.getInstance().update(id, missionType, addedTime, startedTime, allowedTime, taskJson, townName, startedPlayerUUID, completedTime, isClaimed, sprint, season);
                break;
            case MYSQL:
                MissionHistoryDatabase.getInstance().update(id, missionType, addedTime, startedTime, allowedTime, taskJson, townName, startedPlayerUUID, completedTime, isClaimed, sprint, season);
                break;
        }
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<MissionHistoryEntry> getEntries() {
        switch (storageType) {
            case YAML:
                return MissionHistoryYaml.getInstance().getEntries();
            case MYSQL:
                return MissionHistoryDatabase.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public MissionHistoryStorage getInstance() {
        if (singleton == null) {
            new MissionHistoryStorage((TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission"));
        }

        return singleton;
    }
}
