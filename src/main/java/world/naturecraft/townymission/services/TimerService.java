/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

// This service is mainly here to check the progress of sprint and season

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.components.entity.*;
import world.naturecraft.townymission.components.enums.RewardMethod;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.rank.RankJson;
import world.naturecraft.townymission.components.json.rank.TownRankJson;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.json.reward.RewardJson;
import world.naturecraft.townymission.components.json.reward.PointRewardJson;
import world.naturecraft.townymission.config.reward.RewardConfigParser;
import world.naturecraft.townymission.data.dao.*;
import world.naturecraft.townymission.utils.RankUtil;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.*;

/**
 * The type Timer service.
 */
public class TimerService extends TownyMissionService {

    private static TimerService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TimerService getInstance() {
        if (singleton == null) {
            singleton = new TimerService();
        }

        return singleton;
    }

    /**
     * Start season.
     */
    public void startSeason() {
        // Save started time in the config.yml
        Date date = new Date();
        instance.getConfig().set("season.startedTime", date.getTime());
        instance.saveConfig();
    }

    /**
     * Start sprint timer.
     */
    public void startSprintTimer() {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                Date date = new Date();
                long timeNow = date.getTime();

                // This means that season is not started
                if (instance.getConfig().getInt("season.startedTime") == -1) return;

                // This means that we are in season interval
                if (isInInterval(RankType.SEASON)) return;

                // Get some numbers, do some math
                long sprintDuration = Util.hrToMs(instance.getConfig().getInt("sprint.duration") * 24);
                long sprintIntervalDuration = Util.hrToMs(instance.getConfig().getInt("sprint.interval") * 24);
                int currentSprint = instance.getConfig().getInt("sprint.current");
                long seasonStartedTime = instance.getConfig().getLong("season.startedTime");

                long sprintStartedTime = seasonStartedTime + (currentSprint - 1) * (sprintDuration + sprintIntervalDuration);
                long sprintEndTime = seasonStartedTime + currentSprint * sprintDuration;
                long sprintInterEndTime = seasonStartedTime + currentSprint * (sprintDuration + sprintIntervalDuration);

                if (timeNow > sprintInterEndTime) {
                    instance.getLogger().warning("Sprint interval ended, proceeding to the next interval");
                    // This means that we are in the next sprint, change config.yml
                    instance.getConfig().set("sprint.current", currentSprint + 1);
                    instance.saveConfig();
                } else if (timeNow < sprintInterEndTime && timeNow > sprintEndTime) {
                    // This means that sprint is now in the interval, do clean up task before config changes

                    // Check if in season interval, if is, do nothing
                    if (SprintHistoryDao.getInstance().get(instance.getConfig().getInt("season.current"),
                            instance.getConfig().getInt("sprint.current")) != null) {
                            return;
                    }

                    instance.getLogger().warning("Sprint interval reached, doing sprint recess clean up jobs");

                    // Clear MissionStorage
                    List<MissionEntry> entryList = MissionDao.getInstance().getEntries();
                    for (MissionEntry entry : entryList) {
                        if (entry.isStarted() && entry.isCompleted()) {
                            // If it is already completed, but unmoved, move to MissionHistory, and give the reward
                            MissionService.getInstance().completeMission(entry.getTown().getMayor().getPlayer(), entry);
                        }

                        // Remove the entry from MissionStorage
                        MissionDao.getInstance().remove(entry);
                    }

                    // Clear CooldownStorage
                    List<CooldownEntry> cooldownEntries = CooldownDao.getInstance().getEntries();
                    for (CooldownEntry cooldownEntry : cooldownEntries) {
                        CooldownDao.getInstance().remove(cooldownEntry);
                    }

                    // Issue rewards
                    List<SprintEntry> sprintEntries = (List<SprintEntry>) RankUtil.sort(SprintDao.getInstance().getEntries());
                    Map<Integer, List<RewardJson>> rewardsMap = RewardConfigParser.getRankRewardsMap(RankType.SPRINT);

                    for (Integer currentRank : rewardsMap.keySet()) {
                        List<RewardJson> rewardJsonList = rewardsMap.get(currentRank);
                        if (currentRank - 1 < sprintEntries.size()) {
                            SprintEntry sprintEntry = sprintEntries.get(currentRank - 1);
                            Town town = TownyUtil.getTownByName(sprintEntry.getTownName());
                            RewardMethod rewardMethod = RewardConfigParser.getRewardMethods(RankType.SPRINT);

                            for (RewardJson rewardJson : rewardJsonList) {
                                if (rewardJson.getRewardType().equals(RewardType.POINTS)) {
                                    // This is reward season point. Ignore RewardMethod.
                                    if (SeasonDao.getInstance().get(town.getUUID().toString()) == null) {
                                        SeasonDao.getInstance().add(
                                                new SeasonEntry(
                                                        UUID.randomUUID(),
                                                        town.getUUID().toString(),
                                                        town.getName(),
                                                        rewardJson.getAmount(),
                                                        instance.getConfig().getInt("season.current")));
                                    } else {
                                        SeasonEntry seasonEntry = SeasonDao.getInstance().get(town.getUUID().toString());
                                        seasonEntry.setSeasonPoint(seasonEntry.getSeasonPoint() + rewardJson.getAmount());
                                    }
                                } else {
                                    // Giving individual reward. Count in RewardMethod
                                    switch (rewardMethod) {

                                        // TODO: Reward
                                        // TODO: Reward others
                                    }
                                }
                            }
                        }
                    }

                    // Clear SprintStorage, move ranking to SprintHistoryStorage, reward the town based on config
                    List<TownRankJson> townRankJsons = (List<TownRankJson>) RankUtil.sort(SprintDao.getInstance().getEntriesAsJson());
                    RankJson rankJson = new RankJson(RankType.SPRINT, townRankJsons);

                    try {
                        SprintHistoryEntry sprintHistoryEntry =
                                new SprintHistoryEntry(
                                        UUID.randomUUID(),
                                        instance.getConfig().getInt("season.current"),
                                        instance.getConfig().getInt("sprint.current"),
                                        sprintStartedTime,
                                        rankJson.toJson());

                        SprintHistoryDao.getInstance().add(sprintHistoryEntry);
                    } catch (JsonProcessingException exception) {
                        exception.printStackTrace();
                    }

                    for (SprintEntry sprintEntry : SprintDao.getInstance().getEntries()) {
                        SprintDao.getInstance().remove(sprintEntry);
                    }
                }
            }
        };

        // Check every minute for whether the sprint has ended
        r.runTaskTimerAsynchronously(instance, 0, 60 * 20);
    }

    /**
     * Start season timer.
     */
    public void startSeasonTimer() {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                Date date = new Date();
                long timeNow = date.getTime();

                // This means that season is not started
                if (instance.getConfig().getInt("season.startedTime") == -1) return;

                if (timeNow > getTotalEndTime(RankType.SEASON)) {
                    instance.getLogger().warning("Season interval ended. Proceed to the next season.");
                    // This means we are entering next season
                    instance.getConfig().set("season.current", instance.getConfig().getInt("season.current") + 1);
                    instance.getConfig().set("sprint.current", 1);
                    instance.saveConfig();
                } else if (timeNow < getTotalEndTime(RankType.SEASON) && timeNow > getActiveEndTime(RankType.SEASON)) {

                    // If the entry is already in there, do nothing
                    if (SeasonHistoryDao.getInstance().get(instance.getConfig().getInt("season.current")) != null)
                        return;

                    instance.getLogger().warning("Season interval reached. Doing season clean up job");
                    // This means we are in the interval time, do clean up job and issue rewards
                    // Since reaching this point means the sprint has ended, and sprint cleanup job has been done
                    //     - Mission has been moved to MissionHistory
                    //     - Cooldown has been cleared
                    //     - Sprint has been moved to SprintHistory
                    // We only need to do season only jobs

                    // Clean out SeasonStorage, move to SeasonHistoryStorage
                    List<TownRankJson> townRankJsons = (List<TownRankJson>) RankUtil.sort(SeasonDao.getInstance().getEntriesAsJson());
                    RankJson rankJson = new RankJson(RankType.SEASON, townRankJsons);

                    try {
                        SeasonHistoryEntry seasonHistoryEntry =
                                new SeasonHistoryEntry(
                                        UUID.randomUUID(),
                                        instance.getConfig().getInt("season.current"),
                                        getStartTime(RankType.SEASON),
                                        rankJson.toJson()
                                );

                        SeasonHistoryDao.getInstance().add(seasonHistoryEntry);
                    } catch (JsonProcessingException jsonProcessingException) {
                        jsonProcessingException.printStackTrace();
                    }

                    for (SeasonEntry seasonEntry : SeasonDao.getInstance().getEntries()) {
                        SeasonDao.getInstance().remove(seasonEntry);
                    }

                    //TODO: Grant reward
                }
            }
        };

        r.runTaskTimerAsynchronously(instance, 0, 100);
    }

    public boolean canStart() {
        Date date = new Date();
        long timeNow = date.getTime();

        // This means that season is not started
        if (instance.getConfig().getInt("season.startedTime") == -1) return false;

        return !isInInterval(RankType.SEASON) && !isInInterval(RankType.SPRINT);
    }

    public boolean isInInterval(RankType rankType) {
        long timeNow = new Date().getTime();
        return timeNow < getTotalEndTime(rankType) && timeNow > getActiveEndTime(rankType);
    }

    public long getStartTime(RankType rankType) {
        switch (rankType) {
            case SPRINT:
                long seasonStartedTime = instance.getConfig().getLong("season.startedTime");
                int currentSprint = instance.getConfig().getInt("sprint.current");
                long sprintDuration = getDuration(RankType.SPRINT);
                long sprintIntervalDuration = getIntervalDuration(RankType.SPRINT);
                return seasonStartedTime + (currentSprint - 1) * (sprintDuration + sprintIntervalDuration);
            case SEASON:
                return instance.getConfig().getLong("season.startedTime");
        }

        throw new IllegalStateException();
    }

    public long getDuration(RankType rankType) {
        switch (rankType) {
            case SPRINT:
                return Util.hrToMs(instance.getConfig().getInt("sprint.duration") * 24);
            case SEASON:
                long numSprints = instance.getConfig().getInt("season.sprintsPerSeason");
                long sprintDura = Util.hrToMs(instance.getConfig().getInt("sprint.duration") * 24);
                long sprintInterDura = getIntervalDuration(RankType.SPRINT);
                return numSprints * (sprintDura + sprintInterDura);
        }

        throw new IllegalStateException();
    }

    public long getIntervalDuration(RankType rankType) {
        switch (rankType) {
            case SPRINT:
                return Util.hrToMs(instance.getConfig().getInt("sprint.interval") * 24);
            case SEASON:
                return Util.hrToMs(instance.getConfig().getInt("season.interval") * 24);
        }

        throw new IllegalStateException();
    }

    public long getActiveEndTime(RankType rankType) {
        switch (rankType) {
            case SPRINT:
                long seasonStartedTime = getStartTime(RankType.SEASON);
                int currentSprint = instance.getConfig().getInt("sprint.current");
                long sprintDuration = getDuration(RankType.SPRINT);
                return seasonStartedTime + currentSprint * sprintDuration;
            case SEASON:
                return getStartTime(RankType.SEASON) + getDuration(RankType.SEASON);
        }

        throw new IllegalStateException();
    }

    public long getTotalEndTime(RankType rankType) {
        return getActiveEndTime(rankType) + getIntervalDuration(rankType);
    }
}
