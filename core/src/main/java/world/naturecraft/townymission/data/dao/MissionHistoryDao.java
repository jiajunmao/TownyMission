/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.api.exceptions.DataProcessException;
import world.naturecraft.townymission.components.entity.MissionHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.MissionHistoryStorage;
import world.naturecraft.townymission.services.StorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Mission history dao.
 */
public class MissionHistoryDao extends Dao<MissionHistoryEntry> {

    private static MissionHistoryDao singleton;
    private final MissionHistoryStorage db;

    /**
     * Instantiates a new Task dao.
     */
    public MissionHistoryDao() {
        super(StorageService.getInstance().getStorage(DbType.MISSION_HISTORY));
        this.db = StorageService.getInstance().getStorage(DbType.MISSION_HISTORY);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionHistoryDao getInstance() {
        if (singleton == null) {
            singleton = new MissionHistoryDao();
        }
        return singleton;
    }

    /**
     * Gets all unclaimed.
     *
     * @param townUUID the town uuid
     * @return the all unclaimed
     */
    public List<MissionHistoryEntry> getAllUnclaimed(UUID townUUID) {
        List<MissionHistoryEntry> list = db.getEntries();
        List<MissionHistoryEntry> result = new ArrayList<>();

        for (MissionHistoryEntry e : list) {
            if (e.getTownUUID().equals(townUUID) && !e.isClaimed()) {
                result.add(e);
            }
        }

        return result;
    }

    public void add(MissionHistoryEntry entry) {
        db.add(entry.getMissionType().name(),
                entry.getAddedTime(),
                entry.getStartedTime(),
                entry.getAllowedTime(),
                entry.getMissionJson().toJson(),
                entry.getTownUUID(),
                entry.getStartedPlayerUUID(),
                entry.getCompletedTime(),
                entry.isClaimed(),
                entry.getSprint(),
                entry.getSeason());
    }

    public void remove(MissionHistoryEntry entry) {
        db.remove(entry.getId());
    }

    @Override
    public void reloadDb() {
        singleton = new MissionHistoryDao();
    }

    public void update(MissionHistoryEntry entry) {
        db.update(entry.getId(),
                entry.getMissionType().name(),
                entry.getAddedTime(),
                entry.getStartedTime(),
                entry.getAllowedTime(),
                entry.getMissionJson().toJson(),
                entry.getTownUUID(),
                entry.getStartedPlayerUUID(),
                entry.getCompletedTime(),
                entry.isClaimed(),
                entry.getSprint(),
                entry.getSeason());
    }
}
