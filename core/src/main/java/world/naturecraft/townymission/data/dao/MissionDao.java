/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.naturelib.NaturePlugin;
import world.naturecraft.naturelib.database.Dao;
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
        List<MissionEntry> list = new ArrayList<>();
        for (MissionEntry e : db.getEntries()) {
            if (e.getTownUUID().equals(townUUID)) {
                list.add(e);
            }
        }

        return list;
    }

    /**
     * Gets town tasks.
     *
     * @param townUUID    the town uuid
     * @param missionType the mission type
     * @return the town tasks
     */
    @Deprecated
    public MissionEntry getTownStartedMission(UUID townUUID, MissionType missionType) {
        List<MissionEntry> list = getTownMissions(townUUID);

        for (MissionEntry e : list) {
            if (e.getMissionType().equals(missionType) && e.getStartedTime() != 0) {
                return e;
            }
        }

        return null;
    }

    /**
     * Gets started mission.
     *
     * @param townUUID the town uuid
     * @return the started mission
     */
    public MissionEntry getStartedMission(UUID townUUID) {
        for (MissionEntry e : db.getEntries()) {
            if (e.getTownUUID().equals(townUUID)) {
                if (e.getStartedTime() != 0) {
                    return e;
                }
            }
        }

        return null;
    }

    /**
     * Gets started missions.
     *
     * @param townUUID the town uuid
     * @return the started missions
     */
    public List<MissionEntry> getStartedMissions(UUID townUUID) {
        List<MissionEntry> list = getEntries(data -> (data.getTownUUID().equals(townUUID)
                && data.isStarted()));

        return list;
    }

    public List<Integer> getMissingIndexMissions(UUID townUUID) {
        List<MissionEntry> list = getEntries(data -> data.getTownUUID().equals(townUUID));
        List<Integer> finalList = new ArrayList<>();
        int maxMission = TownyMissionInstance.getInstance().getInstanceConfig().getInt("mission.amount");
        for (int i = 0; i < maxMission; i++) {
            finalList.add(i);
        }

        for (MissionEntry e : list) {
            finalList.remove(e.getNumMission());
        }

        return finalList;
    }

    /**
     * This returns the indexed MissionEntry from 1 to mission.amount
     *
     * @param townUUID the town uuid
     * @param index    The index
     * @return The corresponding MissionEntry
     */
    //TODO: nuke this method
    @Deprecated
    public MissionEntry getIndexedMission(UUID townUUID, int index) {
        List<MissionEntry> missionEntries = getTownMissions(townUUID);
        return missionEntries.get(index - 1);
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
