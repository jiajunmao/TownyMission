/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.bukkit.api.exceptions.DataProcessException;
import world.naturecraft.townymission.core.components.entity.MissionHistoryEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.data.db.MissionHistoryStorage;
import world.naturecraft.townymission.core.services.StorageService;

import java.util.ArrayList;
import java.util.List;

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
     * @param town the town
     * @return the all unclaimed
     */
    public List<MissionHistoryEntry> getAllUnclaimed(Town town) {
        List<MissionHistoryEntry> list = db.getEntries();
        List<MissionHistoryEntry> result = new ArrayList<>();

        for (MissionHistoryEntry e : list) {
            if (e.getTown().equals(town) && !e.isClaimed()) {
                result.add(e);
            }
        }

        return result;
    }

    public void add(MissionHistoryEntry entry) {
        try {
            db.add(entry.getMissionType().name(),
                    entry.getAddedTime(),
                    entry.getStartedTime(),
                    entry.getAllowedTime(),
                    entry.getMissionJson().toJson(),
                    entry.getTown().getName(),
                    entry.getStartedPlayer().getUniqueId().toString(),
                    entry.getCompletedTime(),
                    entry.isClaimed(),
                    entry.getSprint(),
                    entry.getSeason());
        } catch (JsonProcessingException e) {
            throw new DataProcessException(e);
        }
    }

    public void remove(MissionHistoryEntry entry) {
        db.remove(entry.getId());
    }

    public void update(MissionHistoryEntry entry) {
        try {
            db.update(entry.getId(),
                    entry.getMissionType().name(),
                    entry.getAddedTime(),
                    entry.getStartedTime(),
                    entry.getAllowedTime(),
                    entry.getMissionJson().toJson(),
                    entry.getTown().getName(),
                    entry.getStartedPlayer().getUniqueId().toString(),
                    entry.getCompletedTime(),
                    entry.isClaimed(),
                    entry.getSprint(),
                    entry.getSeason());
        } catch (JsonProcessingException e) {
            throw new DataProcessException(e);
        }
    }
}
