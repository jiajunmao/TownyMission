/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.events.DoMissionEvent;
import world.naturecraft.townymission.api.exceptions.NoStartedException;
import world.naturecraft.townymission.components.containers.json.MissionJson;
import world.naturecraft.townymission.components.containers.sql.MissionEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.listeners.TownyMissionListener;
import world.naturecraft.townymission.services.MissionService;
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
        MissionEntry missionEntry = e.getTaskEntry();
        MissionJson missionjson = missionEntry.getMissionJson();

        // If a task is not started, or have already timed out, ignore do mission event
        if (missionEntry.getStartedTime() == 0 || Util.isTimedOut(missionEntry)) {
            return;
        }

        if (missionjson.getCompleted() >= missionjson.getAmount()) {
            MissionService.getInstance().completeMission(missionEntry.getTown());
        }
    }
}
