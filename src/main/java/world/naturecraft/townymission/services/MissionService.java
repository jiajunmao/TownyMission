/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.api.exceptions.NotFoundException;
import world.naturecraft.townymission.components.entity.*;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.json.mission.MissionJson;
import world.naturecraft.townymission.data.dao.*;
import world.naturecraft.townymission.utils.EntryFilter;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The type Mission service.
 */
public class MissionService extends TownyMissionService {

    private static MissionService singleton;


    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionService getInstance() {
        if (singleton == null) {
            singleton = new MissionService();
        }
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
                        if (CooldownService.getInstance().isStillInCooldown(town)) {
                            long remainingTime = CooldownService.getInstance().getRemaining(town);
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
            SprintEntry sprintEntry = new SprintEntry(UUID.randomUUID(), town.getUUID().toString(), town.getName(), 0, instance.getStatsConfig().getInt("sprint.current"), instance.getStatsConfig().getInt("season.current"));
            SprintDao.getInstance().add(sprintEntry);
        }

        if (SeasonDao.getInstance().get(town.getUUID().toString()) == null) {
            SeasonEntry seasonEntry = new SeasonEntry(UUID.randomUUID(), town.getUUID().toString(), town.getName(), 0, instance.getStatsConfig().getInt("season.current"));
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
        CooldownService.getInstance().startCooldown(entry.getTown(), Util.minuteToMs(instance.getConfig().getInt("mission.cooldown")));
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
        if (SprintDao.getInstance().contains(missionHistoryEntry.getTown())) {
            SprintEntry sprintEntry = SprintDao.getInstance().get(missionHistoryEntry.getTown().getUUID().toString());
            sprintEntry.setNaturepoints(sprintEntry.getNaturepoints() + missionHistoryEntry.getMissionJson().getReward());
            SprintDao.getInstance().update(sprintEntry);
        } else {
            SprintDao.getInstance().add(new SprintEntry(UUID.randomUUID(),
                    missionHistoryEntry.getTown().getUUID().toString(),
                    missionHistoryEntry.getTown().getName(),
                    missionHistoryEntry.getMissionJson().getReward(),
                    instance.getStatsConfig().getInt("sprint.current"),
                    instance.getStatsConfig().getInt("season.current")));
        }
        CooldownService.getInstance().startCooldown(entry.getTown(), Util.minuteToMs(instance.getConfig().getInt("mission.cooldown")));
    }

    /**
     * Gets average contributions.
     *
     * @param sprint the sprint
     * @param season the season
     * @return the average contributions
     */
    public Map<String, Double> getAverageContributions(int sprint, int season) {
        List<MissionHistoryEntry> missionHistoryEntries = MissionHistoryDao.getInstance().getEntries(new EntryFilter<MissionHistoryEntry>() {
            @Override
            public boolean include(MissionHistoryEntry data) {
                return (data.getSeason() == season && data.getSprint() == sprint);
            }
        });

        Map<String, Double> averageContribution = new HashMap<>();
        for (MissionHistoryEntry missionHistoryEntry : missionHistoryEntries) {
            MissionJson missionJson = missionHistoryEntry.getMissionJson();
            Map<String, Integer> missionContribution = missionJson.getContributions();
            int requiredAmount = missionJson.getAmount();
            for (String player : missionContribution.keySet()) {
                int contribution = missionContribution.get(player);
                double percent = (double) contribution / requiredAmount;

                if (!averageContribution.containsKey(player)) {
                    averageContribution.put(player, percent);
                } else {
                    averageContribution.put(player, averageContribution.get(player) + percent);
                }
            }
        }

        int totalMissions = missionHistoryEntries.size();
        for (String player : averageContribution.keySet()) {
            averageContribution.put(player, averageContribution.get(player) / totalMissions);
        }

        return averageContribution;
    }

    public void sprintEndCleanUp() {
        List<MissionEntry> entryList = MissionDao.getInstance().getEntries();
        for (MissionEntry entry : entryList) {
            if (entry.isStarted() && entry.isCompleted()) {
                // If it is already completed, but unmoved, move to MissionHistory, and give the reward
                MissionService.getInstance().completeMission(entry.getTown().getMayor().getPlayer(), entry);
            }

            // Remove the entry from MissionStorage
            MissionDao.getInstance().remove(entry);
        }
    }
}
