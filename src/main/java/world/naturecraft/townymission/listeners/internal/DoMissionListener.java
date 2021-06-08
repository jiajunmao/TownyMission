/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.events.DoMissionEvent;
import world.naturecraft.townymission.api.exceptions.NotStartedException;
import world.naturecraft.townymission.components.containers.json.MissionJson;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.containers.sql.TaskHistoryEntry;
import world.naturecraft.townymission.listeners.TownyMissionListener;
import world.naturecraft.townymission.utils.Util;

/**
 * The type Do mission listener.
 */
public class DoMissionListener extends TownyMissionListener {

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public DoMissionListener(TownyMission instance) {
        super(instance);
    }

    /**
     * On do mission.
     *
     * @param e the e
     */
    @EventHandler
    public void onDoMission(DoMissionEvent e) {
        TaskEntry taskEntry = e.getTaskEntry();
        Player player = e.getPlayer();
        MissionJson missionjson = taskEntry.getMissionJson();

        // If a task is not started, or have already timed out, ignore do mission event
        try {
            if (taskEntry.getStartedTime() == 0 || Util.isTimedOut(taskEntry)) {
                return;
            }
        } catch (NotStartedException notStartedException) {
            notStartedException.printStackTrace();
            return;
        }

        if (missionjson.getCompleted() >= missionjson.getAmount()) {
            taskDao.remove(taskEntry);
            TaskHistoryEntry taskHistoryEntry = new TaskHistoryEntry(taskEntry, Util.currentTime());
            try {
                taskHistoryDao.add(taskHistoryEntry);
            } catch (JsonProcessingException exception) {
                exception.printStackTrace();
            }
        }
    }
}
