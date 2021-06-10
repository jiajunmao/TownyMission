/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.NoStartedException;
import world.naturecraft.townymission.components.containers.sql.MissionEntry;
import world.naturecraft.townymission.components.containers.sql.MissionHistoryEntry;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.components.containers.sql.SprintEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.dao.*;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.List;

public class MissionService extends TownyMissionService {

    public MissionService(TownyMission instance) {
        super(instance);
    }

    public boolean hasStarted(Town town) {
        return missionDao.getStartedMission(town) == null;
    }

    public void startMission(Player player, int choice) {
        Town town = TownyUtil.residentOf(player);

        List<MissionEntry> taskEntries = missionDao.getTownMissions(town);
        int missionIdx = choice;

        MissionEntry entry = taskEntries.get(missionIdx - 1);
        entry.setStartedTime(Util.currentTime());
        entry.setStartedPlayer(player);
        missionDao.update(entry);

        if (sprintDao.get(town.getUUID().toString()) == null) {
            SprintEntry sprintEntry = new SprintEntry(0, town.getUUID().toString(), town.getName(), 0, instance.getConfig().getInt("sprint.current"), instance.getConfig().getInt("season.current"));
            sprintDao.add(sprintEntry);
        }

        if (seasonDao.get(town.getUUID().toString()) == null) {
            SeasonEntry seasonEntry = new SeasonEntry(0, town.getUUID().toString(), town.getName(), 0, instance.getConfig().getInt("season.current"));
            seasonDao.add(seasonEntry);
        }
    }

    public void abortMission(Town town) {
        if (!hasStarted(town)) {
            throw new NoStartedException(town);
        }

        MissionEntry taskEntry = missionDao.getStartedMission(town);
        missionDao.remove(taskEntry);
        cooldownDao.startCooldown(town, Util.minuteToMs(instance.getConfig().getInt("mission.cooldown")));
    }

    public void completeMission(Town town) {
        if (!hasStarted(town)) {
            throw new NoStartedException(town);
        }

        MissionEntry missionEntry = missionDao.getStartedMission(town);
        missionDao.remove(missionEntry);
        MissionHistoryEntry missionHistoryEntry = new MissionHistoryEntry(missionEntry, Util.currentTime());
        missionHistoryDao.add(missionHistoryEntry);
        cooldownDao.startCooldown(missionEntry.getTown(), Util.minuteToMs(instance.getConfig().getInt("mission.cooldown")));
    }
}
