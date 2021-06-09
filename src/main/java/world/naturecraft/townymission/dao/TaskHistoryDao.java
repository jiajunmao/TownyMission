/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.components.containers.sql.TaskHistoryEntry;
import world.naturecraft.townymission.db.sql.TaskHistoryDatabase;

import java.util.ArrayList;
import java.util.List;

public class TaskHistoryDao extends Dao<TaskHistoryEntry> {

    private final TaskHistoryDatabase db;

    /**
     * Instantiates a new Task dao.
     *
     * @param db the db
     */
    public TaskHistoryDao(TaskHistoryDatabase db) {
        this.db = db;
    }

    public List<TaskHistoryEntry> getAllUnclaimed(Town town) {
        List<TaskHistoryEntry> list = db.getEntries();
        List<TaskHistoryEntry> result = new ArrayList<>();

        for (TaskHistoryEntry e : list) {
            if (e.getTown().equals(town) && !e.isClaimed()) {
                result.add(e);
            }
        }

        return result;
    }

    public void add(TaskHistoryEntry entry) throws JsonProcessingException {
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
    }

    public void remove(TaskHistoryEntry entry) {
        db.remove(entry.getId());
    }

    public void update(TaskHistoryEntry entry) throws JsonProcessingException {
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
    }

    @Override
    public List<TaskHistoryEntry> getEntries() {
        return db.getEntries();
    }
}
