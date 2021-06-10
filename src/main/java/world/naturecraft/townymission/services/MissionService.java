/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.NoStartedException;
import world.naturecraft.townymission.api.exceptions.NotFoundException;
import world.naturecraft.townymission.components.containers.sql.*;
import world.naturecraft.townymission.data.dao.*;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MissionService extends TownyMissionService {

    private static MissionService singleton;

    public MissionService(TownyMission instance) {
        super(instance);
    }

    public boolean canStartMission(Player player) {
        return new SanityChecker(instance).target(player)
                .hasTown()
                .hasPermission("townymission.player")
                .customCheck(() -> {
                    Town town = TownyUtil.residentOf(player);

                    if (MissionDao.getInstance().getStartedMission(town) == null) {
                        return true;
                    } else {
                        Util.sendMsg(player, instance.getLangEntry("commands.start.onAlreadyStarted"));
                        return false;
                    }
                }).customCheck(() -> {
                    Town town = TownyUtil.residentOf(player);

                    try {
                        if (CooldownDao.getInstance().isStillInCooldown(town)) {
                            long remainingTime = CooldownDao.getInstance().getRemaining(town);
                            String display = String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(remainingTime),
                                    TimeUnit.MILLISECONDS.toMinutes(remainingTime) -
                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)));
                            Util.sendMsg(player, instance.getLangEntry("commands.start.onStillInCooldown").replace("%time%", display));
                            return false;
                        } else {
                            return true;
                        }
                    } catch (NotFoundException e) {
                        Date date = new Date();
                        CooldownDao.getInstance().add(new CooldownEntry(0, town, date.getTime(), 0));
                        return true;
                    }
                }).check();
    }

    public boolean hasStarted(Town town) {
        return MissionDao.getInstance().getStartedMission(town) == null;
    }

    public void startMission(Player player, int choice) {
        Town town = TownyUtil.residentOf(player);

        List<MissionEntry> taskEntries = MissionDao.getInstance().getTownMissions(town);
        int missionIdx = choice;

        MissionEntry entry = taskEntries.get(missionIdx - 1);
        entry.setStartedTime(Util.currentTime());
        entry.setStartedPlayer(player);
        MissionDao.getInstance().update(entry);

        if (SprintDao.getInstance().get(town.getUUID().toString()) == null) {
            SprintEntry sprintEntry = new SprintEntry(0, town.getUUID().toString(), town.getName(), 0, instance.getConfig().getInt("sprint.current"), instance.getConfig().getInt("season.current"));
            SprintDao.getInstance().add(sprintEntry);
        }

        if (SeasonDao.getInstance().get(town.getUUID().toString()) == null) {
            SeasonEntry seasonEntry = new SeasonEntry(0, town.getUUID().toString(), town.getName(), 0, instance.getConfig().getInt("season.current"));
            SeasonDao.getInstance().add(seasonEntry);
        }
    }

    public void abortMission(Town town) {
        if (!hasStarted(town)) {
            throw new NoStartedException(town);
        }

        MissionEntry taskEntry = MissionDao.getInstance().getStartedMission(town);
        MissionDao.getInstance().remove(taskEntry);
        CooldownDao.getInstance().startCooldown(town, Util.minuteToMs(instance.getConfig().getInt("mission.cooldown")));
    }

    public void completeMission(Town town) {
        if (!hasStarted(town)) {
            throw new NoStartedException(town);
        }

        MissionEntry missionEntry = MissionDao.getInstance().getStartedMission(town);
        MissionDao.getInstance().remove(missionEntry);
        MissionHistoryEntry missionHistoryEntry = new MissionHistoryEntry(missionEntry, Util.currentTime());
        MissionHistoryDao.getInstance().add(missionHistoryEntry);
        CooldownDao.getInstance().startCooldown(missionEntry.getTown(), Util.minuteToMs(instance.getConfig().getInt("mission.cooldown")));
    }

    public static MissionService getInstance() {
        return singleton;
    }
}
