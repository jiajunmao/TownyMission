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
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.dao.*;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * The type Mission service.
 */
public class MissionService extends TownyMissionService {

    private static MissionService singleton;

    /**
     * Instantiates a new Mission service.
     *
     * @param instance the instance
     */
    public MissionService(TownyMission instance) {
        super(instance);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionService getInstance() {
        return singleton;
    }

    /**
     * Can start mission boolean.
     *
     * @param player the player
     * @return the boolean
     */
    private boolean canStartMission(Player player) {
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
                        // Not starting, only putting them into the db
                        CooldownDao.getInstance().add(new CooldownEntry(UUID.randomUUID(), town, date.getTime(), 0));
                        return true;
                    }
                }).check();
    }

    private boolean canAbortMission(Player player, MissionEntry entry) {
        SanityChecker checker = new SanityChecker(instance).target(player)
                .hasTown()
                .hasStarted()
                .hasPermission("townymission.player")
                .customCheck(() -> {
                    if (TownyUtil.mayorOf(player) != null)
                        return true;

                    return entry.getStartedPlayer().equals(player);
                });

        return checker.check();
    }

    /**
     * Has started boolean.
     *
     * @param town the town
     * @return the boolean
     */
    public boolean hasStarted(Town town) {
        return MissionDao.getInstance().getStartedMission(town) == null;
    }

    /**
     * Start mission.
     *
     * @param player the player
     * @param choice the choice
     * @return the boolean
     */
    public boolean startMission(Player player, int choice) {
        if (!canStartMission(player))
            return false;

        Town town = TownyUtil.residentOf(player);

        List<MissionEntry> taskEntries = MissionDao.getInstance().getTownMissions(town);
        int missionIdx = choice;

        MissionEntry entry = taskEntries.get(missionIdx - 1);

        entry.setStartedTime(Util.currentTime());
        entry.setStartedPlayer(player);
        MissionDao.getInstance().update(entry);

        if (SprintDao.getInstance().get(town.getUUID().toString()) == null) {
            SprintEntry sprintEntry = new SprintEntry(UUID.randomUUID(), town.getUUID().toString(), town.getName(), 0, instance.getConfig().getInt("sprint.current"), instance.getConfig().getInt("season.current"));
            SprintDao.getInstance().add(sprintEntry);
        }

        if (SeasonDao.getInstance().get(town.getUUID().toString()) == null) {
            SeasonEntry seasonEntry = new SeasonEntry(UUID.randomUUID(), town.getUUID().toString(), town.getName(), 0, instance.getConfig().getInt("season.current"));
            SeasonDao.getInstance().add(seasonEntry);
        }

        return true;
    }

    /**
     * Abort mission.
     *
     * @param player the player
     * @param entry  the entry
     */
    public void abortMission(Player player, MissionEntry entry) {
        if (!canAbortMission(player, entry))
            return;

        MissionDao.getInstance().remove(entry);
        CooldownDao.getInstance().startCooldown(entry.getTown(), Util.minuteToMs(instance.getConfig().getInt("mission.cooldown")));
    }

    /**
     * Complete mission.
     *
     * @param player the player
     * @param entry  the entry
     */
    public void completeMission(Player player, MissionEntry entry) {
        if (entry.isTimedout() && !entry.isCompleted()) return;

        MissionDao.getInstance().remove(entry);
        MissionHistoryEntry missionHistoryEntry = new MissionHistoryEntry(entry, Util.currentTime());
        MissionHistoryDao.getInstance().add(missionHistoryEntry);
        CooldownDao.getInstance().startCooldown(entry.getTown(), Util.minuteToMs(instance.getConfig().getInt("mission.cooldown")));
    }

    /**
     * Gets started missions.
     *
     * @param town the town
     * @return the started missions
     */
    public List<MissionEntry> getStartedMissions(Town town) {
        List<MissionEntry> entryList = new ArrayList<>();
        for (MissionEntry e : MissionDao.getInstance().getEntries()) {
            if (e.isStarted()) {
                entryList.add(e);
            }
        }
        return entryList;
    }

    /**
     * Gets started missions.
     *
     * @param town        the town
     * @param missionType the mission type
     * @return the started missions
     */
    public List<MissionEntry> getStartedMissions(Town town, MissionType missionType) {
        List<MissionEntry> missionEntries = getStartedMissions(town);
        List<MissionEntry> finalList = new ArrayList<>();
        for (MissionEntry entry : missionEntries) {
            if (entry.getMissionType().equals(missionType)) {
                finalList.add(entry);
            }
        }
        return finalList;
    }

    /**
     * This returns the indexed MissionEntry from 1 to mission.amount
     *
     * @param town  The town for the index mission
     * @param index The index
     * @return The corresponding MissionEntry
     */
    public MissionEntry getIndexedMission(Town town, int index) {
        List<MissionEntry> missionEntries = MissionDao.getInstance().getEntries();
        return missionEntries.get(index - 1);
    }
}
