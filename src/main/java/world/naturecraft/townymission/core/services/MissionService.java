/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.services.MissionBukkitService;
import world.naturecraft.townymission.core.components.entity.MissionEntry;
import world.naturecraft.townymission.core.components.entity.MissionHistoryEntry;
import world.naturecraft.townymission.core.components.entity.SeasonEntry;
import world.naturecraft.townymission.core.components.entity.SprintEntry;
import world.naturecraft.townymission.core.components.json.mission.MissionJson;
import world.naturecraft.townymission.core.data.dao.MissionDao;
import world.naturecraft.townymission.core.data.dao.MissionHistoryDao;
import world.naturecraft.townymission.core.data.dao.SeasonDao;
import world.naturecraft.townymission.core.data.dao.SprintDao;
import world.naturecraft.townymission.core.utils.EntryFilter;
import world.naturecraft.townymission.core.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type Mission service.
 */
public abstract class MissionService extends TownyMissionService {

    private static MissionService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionService getInstance() {
        if (singleton == null) {
            if (TownyMissionInstanceType.isBukkit()) {
                singleton = MissionBukkitService.getInstance();
            }
        }
        return singleton;
    }

    /**
     * Can start mission boolean.
     *
     * @param playerUUID the player uuid
     * @return the boolean
     */
    public abstract boolean canStartMission(UUID playerUUID);

    /**
     * Can abort mission boolean.
     *
     * @param playerUUID the player uuid
     * @param entry      the entry
     * @return the boolean
     */
    public abstract boolean canAbortMission(UUID playerUUID, MissionEntry entry);

    /**
     * Has started boolean.
     *
     * @param townUUID the town
     * @return the boolean
     */
    public boolean hasStarted(UUID townUUID) {
        return MissionDao.getInstance().getStartedMission(townUUID) == null;
    }

    /**
     * Start mission.
     *
     * @param playerUUID the player
     * @param choice     the choice
     * @return the boolean
     */
    public boolean startMission(UUID playerUUID, int choice) {
        if (!canStartMission(playerUUID))
            return false;

        UUID townUUID = TownyService.getInstance().residentOf(playerUUID);

        List<MissionEntry> taskEntries = MissionDao.getInstance().getTownMissions(townUUID);
        int missionIdx = choice;

        MissionEntry entry = taskEntries.get(missionIdx - 1);

        entry.setStartedTime(Util.currentTime());
        entry.setStartedPlayerUUID(playerUUID);
        MissionDao.getInstance().update(entry);

        if (SprintDao.getInstance().get(townUUID) == null) {
            SprintEntry sprintEntry = new SprintEntry(
                    UUID.randomUUID(),
                    townUUID,
                    0,
                    instance.getStatsConfig().getInt("sprint.current"),
                    instance.getStatsConfig().getInt("season.current"));
            SprintDao.getInstance().add(sprintEntry);
        }

        if (SeasonDao.getInstance().get(townUUID) == null) {
            SeasonEntry seasonEntry = new SeasonEntry(UUID.randomUUID(), townUUID, 0, instance.getStatsConfig().getInt("season.current"));
            SeasonDao.getInstance().add(seasonEntry);
        }

        return true;
    }

    /**
     * Abort mission.
     *
     * @param playerUUID the player
     * @param entry      the entry
     */
    public void abortMission(UUID playerUUID, MissionEntry entry) {
        if (!canAbortMission(playerUUID, entry))
            return;

        MissionDao.getInstance().remove(entry);
        CooldownService.getInstance().startCooldown(entry.getTownUUID(), Util.minuteToMs(instance.getInstanceConfig().getInt("mission.cooldown")));
    }

    /**
     * Complete mission.
     *
     * @param entry the entry
     */
    public void completeMission(MissionEntry entry) {
        if (entry.isTimedout() && !entry.isCompleted()) return;

        MissionDao.getInstance().remove(entry);
        MissionHistoryEntry missionHistoryEntry = new MissionHistoryEntry(entry, Util.currentTime());
        MissionHistoryDao.getInstance().add(missionHistoryEntry);
        if (SprintDao.getInstance().contains(missionHistoryEntry.getTownUUID())) {
            SprintEntry sprintEntry = SprintDao.getInstance().get(missionHistoryEntry.getTownUUID());
            sprintEntry.setNaturepoints(sprintEntry.getNaturepoints() + missionHistoryEntry.getMissionJson().getReward());
            SprintDao.getInstance().update(sprintEntry);
        } else {
            SprintDao.getInstance().add(new SprintEntry(
                    UUID.randomUUID(),
                    missionHistoryEntry.getTownUUID(),
                    missionHistoryEntry.getMissionJson().getReward(),
                    instance.getStatsConfig().getInt("sprint.current"),
                    instance.getStatsConfig().getInt("season.current")));
        }
        CooldownService.getInstance().startCooldown(entry.getTownUUID(), Util.minuteToMs(instance.getInstanceConfig().getInt("mission.cooldown")));
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

    /**
     * Sprint end clean up.
     */
    public void sprintEndCleanUp() {
        List<MissionEntry> entryList = MissionDao.getInstance().getEntries();
        for (MissionEntry entry : entryList) {
            if (entry.isStarted() && entry.isCompleted()) {
                // If it is already completed, but unmoved, move to MissionHistory, and give the reward
                MissionService.getInstance().completeMission(entry);
            }

            // Remove the entry from MissionStorage
            MissionDao.getInstance().remove(entry);
        }
    }
}
