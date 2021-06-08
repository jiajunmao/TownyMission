/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.db.sql.TaskDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Task dao.
 */
public class TaskDao extends Dao<TaskEntry> {

    private final TaskDatabase db;

    /**
     * Instantiates a new Task dao.
     *
     * @param db the db
     */
    public TaskDao(TaskDatabase db) {
        this.db = db;
    }

    /**
     * Gets num added.
     *
     * @param town the town
     * @return the num added
     */
    public int getNumAdded(Town town) {
        int num = 0;
        for (TaskEntry e : db.getEntries()) {
            if (e.getTown().equals(town)) {
                num++;
            }
        }

        return num;
    }

    /**
     * Gets town tasks.
     *
     * @param town the town
     * @return the town tasks
     */
    public List<TaskEntry> getTownTasks(Town town) {
        List<TaskEntry> list = new ArrayList<>();
        for (TaskEntry e : db.getEntries()) {
            if (e.getTown().equals(town)) {
                list.add(e);
            }
        }

        return list;
    }

    /**
     * Gets town tasks.
     *
     * @param town        the town
     * @param missionType the mission type
     * @return the town tasks
     */
    public TaskEntry getTownStartedMission(Town town, MissionType missionType) {
        List<TaskEntry> list = getTownTasks(town);

        for (TaskEntry e : list) {
            if (e.getMissionType().equals(missionType) && e.getStartedTime() != 0) {
                return e;
            }
        }

        return null;
    }

    /**
     * Gets started mission.
     *
     * @param town the town
     * @return the started mission
     */
    public TaskEntry getStartedMission(Town town) {
        for (TaskEntry e : db.getEntries()) {
            if (e.getTown().equals(town)) {
                if (e.getStartedTime() != 0) {
                    return e;
                }
            }
        }

        return null;
    }

    /**
     * Add.
     *
     * @param entry the entry
     * @throws JsonProcessingException the json processing exception
     */
    public void add(TaskEntry entry) throws JsonProcessingException {
        db.add(entry.getMissionType().name(), entry.getAddedTime(), entry.getStartedTime(), entry.getAllowedTime(), entry.getMissionJson().toJson(), entry.getTown().getName(), entry.getStartedPlayer() == null ? null : entry.getStartedPlayer().getUniqueId().toString());
    }

    /**
     * Remove.
     *
     * @param entry the entry
     */
    public void remove(TaskEntry entry) {
        db.remove(entry.getId());
    }

    /**
     * Update.
     *
     * @param entry the entry
     * @throws JsonProcessingException the json processing exception
     */
    public void update(TaskEntry entry) throws JsonProcessingException {
        db.update(entry.getId(), entry.getMissionType().name(), entry.getAddedTime(), entry.getStartedTime(), entry.getAllowedTime(), entry.getMissionJson().toJson(), entry.getTown().getName(), entry.getStartedPlayer().getUniqueId().toString());
    }
}
