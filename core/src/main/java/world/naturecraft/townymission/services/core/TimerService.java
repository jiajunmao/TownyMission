/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services.core;

// This service is mainly here to check the progress of sprint and season

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.components.entity.SeasonEntry;
import world.naturecraft.townymission.components.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.components.entity.SprintEntry;
import world.naturecraft.townymission.components.entity.SprintHistoryEntry;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.enums.RewardMethod;
import world.naturecraft.townymission.components.json.rank.RankJson;
import world.naturecraft.townymission.components.json.rank.TownRankJson;
import world.naturecraft.townymission.data.dao.*;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.services.TaskService;
import world.naturecraft.townymission.services.TownyMissionService;
import world.naturecraft.townymission.utils.RankUtil;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        instance.getStatsConfig().set("season.startedTime", date.getTime());
        instance.getStatsConfig().save();
    }

    /**
     * Start sprint timer.
     */
    public void startSprintTimer() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                long timeNow = date.getTime();

                //System.out.printf("Curr sprint: %d\n", instance.getStatsConfig().getInt("sprint.current"));
                //System.out.printf("Sprint interval: %b\n", isInInterval(RankType.SPRINT));
                //System.out.printf("Sprint within TotalEndTime: %b, within ActiveEndTime: %b\n", timeNow < getTotalEndTime(RankType.SPRINT), timeNow < getActiveEndTime(RankType.SPRINT));

                // This means that season is not started
                if (instance.getStatsConfig().getLong("season.startedTime") == -1) return;

                // This means that we are in season interval
                if (isInInterval(RankType.SEASON)) return;


                if (timeNow > getTotalEndTime(RankType.SPRINT)) {
                    // If it is the last sprint of the season, do not increment
                    int sprintsPerSeason = instance.getInstanceConfig().getInt("season.sprintsPerSeason");
                    int currSprint = instance.getStatsConfig().getInt("sprint.current");
                    if (currSprint == sprintsPerSeason) return;

                    instance.getInstanceLogger().warning("Sprint interval ended, proceeding to the next interval");
                    // This means that we are in the next sprint, change config.yml
                    instance.getStatsConfig().set("sprint.current", instance.getStatsConfig().getInt("sprint.current") + 1);
                    instance.getStatsConfig().save();

                } else if (timeNow < getTotalEndTime(RankType.SPRINT) && timeNow > getActiveEndTime(RankType.SPRINT)) {
                    // This means that sprint is now in the interval, do clean up task before config changes

                    // **Run this even when it is season interval to clean out sprint related stuff

                    // If a clean task has already been performed, do nothing (If there is current season/sprint entry in SprintHistory)
                    if (SprintHistoryDao.getInstance().get(instance.getStatsConfig().getInt("season.current"),
                            instance.getStatsConfig().getInt("sprint.current")) != null) {
                        return;
                    }

                    instance.getInstanceLogger().warning("Sprint interval reached, doing sprint recess clean up jobs");

                    // Clear MissionStorage
                    instance.getInstanceLogger().info(ChatService.getInstance().translateColor("{#E9B728}===> Cleaning up mission storage"));
                    MissionService.getInstance().sprintEndCleanUp();

                    // Clear CooldownStorage
                    instance.getInstanceLogger().info(ChatService.getInstance().translateColor("{#E9B728}===> Cleaning up cooldown storage"));
                    CooldownDao.getInstance().removeAllEntries();

                    // Issue rewards
                    instance.getInstanceLogger().info(ChatService.getInstance().translateColor("{#E9B728}===> Issuing rewards"));
                    RewardMethod rewardMethod = RewardMethod.valueOf(instance.getInstanceConfig().getString("sprint.rewards.method").toUpperCase(Locale.ROOT));
                    RewardService.getInstance().rewardAllTowns(RankType.SPRINT, rewardMethod);

                    // Clear SprintStorage, move ranking to SprintHistoryStorage
                    sprintEndCleanUp();
                }
            }
        };

        // Check every minute for whether the sprint has ended
        TaskService.getInstance().runTimerTaskAsync(r, 0, 60 * 20);
    }

    /**
     * Start season timer.
     */
    public void startSeasonTimer() {
        Runnable r = new Runnable() {
            @Override
            public void run() {

                Date date = new Date();
                long timeNow = date.getTime();
                //System.out.printf("Curr season: %d, sprint: %d\n", instance.getStatsConfig().getInt("season.current"), instance.getStatsConfig().getInt("sprint.current"));
                //System.out.printf("Season interval: %b, sprint interval %b\n", isInInterval(RankType.SEASON), isInInterval(RankType.SPRINT));
                //System.out.printf("Season within TotalEndTime: %b, within ActiveEndTime: %b\n", timeNow < getTotalEndTime(RankType.SEASON), timeNow < getActiveEndTime(RankType.SEASON));

                // This means that season is not started
                if (instance.getStatsConfig().getLong("season.startedTime") == -1) return;

                if (timeNow > getTotalEndTime(RankType.SEASON)) {
                    instance.getInstanceLogger().warning("Season interval ended. Proceed to the next season.");
                    // This means we are entering next season
                    instance.getStatsConfig().set("season.current", instance.getStatsConfig().getInt("season.current") + 1);
                    instance.getStatsConfig().set("sprint.current", 1);
                    instance.getStatsConfig().save();
                } else if (timeNow < getTotalEndTime(RankType.SEASON) && timeNow > getActiveEndTime(RankType.SEASON)) {

                    // If a clean up job is already finished, do nothing
                    if (SeasonHistoryDao.getInstance().get(instance.getStatsConfig().getInt("season.current")) != null)
                        return;

                    // Check whether the sprint clean up is done, if not, wait til the next iteration
                    if (SprintHistoryDao.getInstance().get(instance.getStatsConfig().getInt("season.current"),
                            instance.getStatsConfig().getInt("sprint.current")) == null)
                        return;

                    instance.getInstanceLogger().warning("Season interval reached. Doing season clean up job");
                    //     - Reward all towns
                    //     - Mission has been moved to MissionHistory
                    //     - Cooldown has been cleared
                    //     - Sprint has been moved to SprintHistory

                    // Clear MissionStorage
                    instance.getInstanceLogger().info(ChatService.getInstance().translateColor("{#E9B728}===> Cleaning up mission storage"));
                    MissionService.getInstance().sprintEndCleanUp();

                    // Clear CooldownStorage
                    instance.getInstanceLogger().info(ChatService.getInstance().translateColor("{#E9B728}===> Cleaning up cooldown storage"));
                    CooldownDao.getInstance().removeAllEntries();

                    // Reward all towns
                    RewardMethod rewardMethod = RewardMethod.valueOf(instance.getInstanceConfig().getString("season.rewards.method").toUpperCase(Locale.ROOT));
                    RewardService.getInstance().rewardAllTowns(RankType.SEASON, rewardMethod);

                    // Clean out SeasonStorage, move to SeasonHistoryStorage
                    seasonEndCleanUp();
                }
            }
        };

        TaskService.getInstance().runTimerTaskAsync(r, 0, 20 * 60);
    }

    /**
     * Season end clean up.
     */
    public void seasonEndCleanUp() {
        List<TownRankJson> townRankJsons = (List<TownRankJson>) RankUtil.sort(SeasonDao.getInstance().getEntriesAsJson());
        RankJson rankJson = new RankJson(RankType.SEASON, townRankJsons);

        try {
            SeasonHistoryEntry seasonHistoryEntry =
                    new SeasonHistoryEntry(
                            UUID.randomUUID(),
                            instance.getStatsConfig().getInt("season.current"),
                            TimerService.getInstance().getStartTime(RankType.SEASON),
                            rankJson.toJson()
                    );

            SeasonHistoryDao.getInstance().add(seasonHistoryEntry);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }

        for (SeasonEntry seasonEntry : SeasonDao.getInstance().getEntries()) {
            SeasonDao.getInstance().remove(seasonEntry);
        }
    }

    /**
     * Sprint end clean up.
     */
    public void sprintEndCleanUp() {
        List<TownRankJson> townRankJsons = (List<TownRankJson>) RankUtil.sort(SprintDao.getInstance().getEntriesAsJson());
        RankJson rankJson = new RankJson(RankType.SPRINT, townRankJsons);

        try {
            SprintHistoryEntry sprintHistoryEntry =
                    new SprintHistoryEntry(
                            UUID.randomUUID(),
                            instance.getStatsConfig().getInt("season.current"),
                            instance.getStatsConfig().getInt("sprint.current"),
                            TimerService.getInstance().getStartTime(RankType.SPRINT),
                            rankJson.toJson());

            SprintHistoryDao.getInstance().add(sprintHistoryEntry);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }

        for (SprintEntry sprintEntry : SprintDao.getInstance().getEntries()) {
            SprintDao.getInstance().remove(sprintEntry);
        }
    }


    /**
     * Can start boolean.
     *
     * @return the boolean
     */
    public boolean canStart() {
        Date date = new Date();

        // This means that season is not started
        if (instance.getStatsConfig().getLong("season.startedTime") == -1) return false;

        if (instance.getStatsConfig().getLong("season.pausedTime") != -1) return false;

        return !isInInterval(RankType.SEASON) && !isInInterval(RankType.SPRINT);
    }

    /**
     * Is in interval boolean.
     *
     * @param rankType the rank type
     * @return the boolean
     */
    public boolean isInInterval(RankType rankType) {
        long timeNow = new Date().getTime();
        return timeNow < getTotalEndTime(rankType) && timeNow > getActiveEndTime(rankType);
    }

    /**
     * Gets start time.
     *
     * @param rankType the rank type
     * @return the start time
     */
    public long getStartTime(RankType rankType) {
        switch (rankType) {
            case SPRINT:
                long seasonStartedTime = instance.getStatsConfig().getLong("season.startedTime");
                int currentSprint = instance.getStatsConfig().getInt("sprint.current");
                long sprintDuration = getDuration(RankType.SPRINT);
                long sprintIntervalDuration = getIntervalDuration(RankType.SPRINT);
                return seasonStartedTime + (currentSprint - 1) * (sprintDuration + sprintIntervalDuration);
            case SEASON:
                return instance.getStatsConfig().getLong("season.startedTime");
        }

        throw new IllegalStateException();
    }

    /**
     * Gets duration.
     *
     * @param rankType the rank type
     * @return the duration
     */
    public long getDuration(RankType rankType) {
        switch (rankType) {
            case SPRINT:
                return TimeUnit.MILLISECONDS.convert(instance.getInstanceConfig().getInt("sprint.duration"), TimeUnit.DAYS);
            case SEASON:
                long numSprints = instance.getInstanceConfig().getInt("season.sprintsPerSeason");
                long sprintDura = TimeUnit.MILLISECONDS.convert(instance.getInstanceConfig().getInt("sprint.duration"), TimeUnit.DAYS);
                long sprintInterDura = getIntervalDuration(RankType.SPRINT);
                return numSprints * (sprintDura + sprintInterDura);
        }

        throw new IllegalStateException();
    }

    /**
     * Gets interval duration.
     *
     * @param rankType the rank type
     * @return the interval duration
     */
    public long getIntervalDuration(RankType rankType) {
        switch (rankType) {
            case SPRINT:
                return TimeUnit.MILLISECONDS.convert(instance.getInstanceConfig().getInt("sprint.interval"), TimeUnit.DAYS);
            case SEASON:
                return TimeUnit.MILLISECONDS.convert(instance.getInstanceConfig().getInt("season.interval"), TimeUnit.DAYS);
        }

        throw new IllegalStateException();
    }

    /**
     * Gets active end time.
     *
     * @param rankType the rank type
     * @return the active end time
     */
    public long getActiveEndTime(RankType rankType) {
        switch (rankType) {
            case SPRINT:
                long seasonStartedTime = getStartTime(RankType.SEASON);
                int currentSprint = instance.getStatsConfig().getInt("sprint.current");
                long sprintDuration = getDuration(RankType.SPRINT);
                return seasonStartedTime + currentSprint * sprintDuration;
            case SEASON:
                return getStartTime(RankType.SEASON) + getDuration(RankType.SEASON);
        }

        throw new IllegalStateException();
    }

    /**
     * Gets total end time.
     *
     * @param rankType the rank type
     * @return the total end time
     */
    public long getTotalEndTime(RankType rankType) {
        return getActiveEndTime(rankType) + getIntervalDuration(rankType);
    }
}
