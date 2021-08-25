/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.naturelib.database.Dao;
import world.naturecraft.naturelib.utils.EntryFilter;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.storage.MissionStorage;
import world.naturecraft.townymission.services.StorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Task dao.
 */
public class MissionDao extends Dao<MissionEntry> {

    private static MissionDao singleton;
    private final MissionStorage db;

    /**
     * Instantiates a new Task dao.
     */
    public MissionDao() {
        super(StorageService.getInstance().getStorage(DbType.MISSION));
        this.db = StorageService.getInstance().getStorage(DbType.MISSION);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionDao getInstance() {
        if (singleton == null) {
            singleton = new MissionDao();
        }
        return singleton;
    }

    /**
     * Gets town tasks.
     *
     * @param townUUID the town uuid
     * @return the town tasks
     */
    public List<MissionEntry> getTownMissions(UUID townUUID) {
        return getEntries(missionEntry -> (missionEntry.getTownUUID().equals(townUUID)));
    }

    public List<MissionEntry> getTownMissions(UUID townUUID, EntryFilter<MissionEntry> filter) {
        return getEntries(missionEntry -> (missionEntry.getTownUUID().equals(townUUID) && filter.include(missionEntry)));
    }

    /**
     * Gets started missions.
     *
     * @param townUUID the town uuid
     * @return the started missions
     */
    public List<MissionEntry> getStartedMissions(UUID townUUID) {
        return getEntries(data -> (data.getTownUUID().equals(townUUID)
                && data.isStarted()));
    }

    public List<Integer> getMissingIndexMissions(UUID townUUID) {
        List<MissionEntry> list = getEntries(data -> data.getTownUUID().equals(townUUID));
        List<Integer> finalList = new ArrayList<>();
        int maxMission = TownyMissionInstance.getInstance().getInstanceConfig().getInt("mission.amount");
        for (int i = 0; i < maxMission; i++) {
            finalList.add(i);
        }

        for (MissionEntry e : list) {
            finalList.remove(Integer.valueOf(e.getNumMission()));
        }

        return finalList;
    }

    /**
     * Add.
     *
     * @param entry the entry
     */
    public void add(MissionEntry entry) {
        db.add(
                entry.getMissionType().name(),
                entry.getAddedTime(),
                entry.getStartedTime(),
                entry.getAllowedTime(),
                entry.getMissionJson().toJson(),
                entry.getTownUUID(),
                entry.getStartedPlayerUUID(),
                entry.getNumMission());
    }

    /**
     * Remove.
     *
     * @param entry the entry
     */
    public void remove(MissionEntry entry) {
        db.remove(entry.getId());
    }

    /**
     * Update.
     *
     * @param entry the entry
     */
    public void update(MissionEntry entry) {
        db.update(
                entry.getId(),
                entry.getMissionType().name(),
                entry.getAddedTime(),
                entry.getStartedTime(),
                entry.getAllowedTime(),
                entry.getMissionJson().toJson(),
                entry.getTownUUID(),
                entry.getStartedPlayerUUID(),
                entry.getNumMission());
    }
}
