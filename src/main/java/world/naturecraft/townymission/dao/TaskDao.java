/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.dao;

import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.db.sql.TaskDatabase;

import java.util.ArrayList;
import java.util.List;

public class TaskDao extends Dao<TaskEntry> {

    private TaskDatabase db;

    public TaskDao(TaskDatabase db) {
        this.db = db;
    }

    public int getNumAdded(Town town) {
        int num = 0;
        for (TaskEntry e : db.getEntries()) {
            if (e.getTown().equalsIgnoreCase(town.getName())) {
                num++;
            }
        }

        return num;
    }

    public List<TaskEntry> getTownTasks(Town town) {
        List<TaskEntry> list = new ArrayList<>();
        for (TaskEntry e : db.getEntries()) {
            if (e.getTown().equalsIgnoreCase(town.getName())) {
                list.add(e);
            }
        }

        return list;
    }

    public List<TaskEntry> getTownTasks(Town town, MissionType missionType) {
        List<TaskEntry> list = getTownTasks(town);
        List<TaskEntry> filtered = new ArrayList<>();

        for (TaskEntry e : list) {
            if (e.getTaskType().equalsIgnoreCase(missionType.name())) {
                filtered.add(e);
            }
        }

        return filtered;
    }

    public boolean hasStartedMission(Town town) {
        for (TaskEntry e : db.getEntries()) {
            if (e.getTown().equalsIgnoreCase(town.getName())) {
                if (e.getStartedTime() != 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public void add(TaskEntry entry) {
        db.add(entry);
    }

    public void remove(TaskEntry entry) {
        db.remove(entry);
    }

    public void update(TaskEntry entry) {
        db.update(entry);
    }
}
