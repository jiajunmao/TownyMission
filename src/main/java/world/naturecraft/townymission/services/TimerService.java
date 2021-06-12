/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

// This service is mainly here to check the progress of sprint and season

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.entity.*;
import world.naturecraft.townymission.components.containers.json.RankJson;
import world.naturecraft.townymission.components.containers.json.TownRankJson;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.data.dao.*;
import world.naturecraft.townymission.utils.RankUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

public class TimerService extends TownyMissionService {

    private static TimerService singleton;

    public static TimerService getInstance() {
        if (singleton == null) {
            singleton = new TimerService();
        }

        return singleton;
    }

    public void startSeason() {
        // Save started time in the config.yml
        Date date = new Date();
        instance.getConfig().set("season.startedTime", date.getTime());
        instance.saveConfig();
    }

    public void startSprintTimer() {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                Date date = new Date();
                long timeNow = date.getTime();

                // Get some numbers, do some math
                long sprintDuration = Util.hrToMs(instance.getConfig().getInt("sprint.duration")*24);
                long sprintIntervalDuration = Util.hrToMs(instance.getConfig().getInt("sprint.interval")*24);
                int currentSprint = instance.getConfig().getInt("sprint.current");
                long seasonStartedTime = instance.getConfig().getLong("season.startedTime");

                long sprintStartedTime = seasonStartedTime + (currentSprint - 1) * (sprintDuration + sprintIntervalDuration);
                long sprintEndTime = seasonStartedTime + currentSprint * sprintDuration;
                long sprintInterEndTime = seasonStartedTime + currentSprint * (sprintDuration + sprintIntervalDuration);

                if (timeNow > sprintInterEndTime) {
                    // This means that we are in the next sprint, change config.yml
                    instance.getConfig().set("sprint.current", currentSprint + 1);
                    instance.saveConfig();
                } else if (timeNow < sprintInterEndTime && timeNow > sprintEndTime) {
                    // This means that sprint is now in the interval, do clean up task before config changes
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

                    //TODO: Issue rewards
                }
            }
        };

        // Check every minute for whether the sprint has ended
        r.runTaskTimerAsynchronously(instance, 0, Util.msToTicks(Util.minuteToMs(1)));
    }

    public void startSeasonTimer() {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                Date date = new Date();
                long timeNow = date.getTime();

                long numSprints = instance.getConfig().getInt("season.sprintsPerSeason");
                long sprintDuration = Util.hrToMs((instance.getConfig().getInt("sprint.duration") + instance.getConfig().getInt("sprint.interval"))*24);
                long seasonIntervalDuration = Util.hrToMs(instance.getConfig().getInt("season.interval") * 24);
                long seasonStartedTime = instance.getConfig().getLong("season.startedTime");
                long seasonDuration = numSprints * sprintDuration;
                long seasonEndTime = seasonStartedTime + seasonDuration;
                long seasonInterEndTime = seasonEndTime + seasonIntervalDuration;

                if (timeNow > seasonInterEndTime) {
                    // This means we are entering next season
                    instance.getConfig().set("season.current", instance.getConfig().getInt("season.current") + 1);
                    instance.getConfig().set("sprint.current", 1);
                    instance.saveConfig();
                } else if (timeNow < seasonInterEndTime && timeNow > seasonEndTime) {
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
                                        seasonStartedTime,
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

        r.runTaskTimerAsynchronously(instance, 0, Util.msToTicks(Util.minuteToMs(1)));
    }
}
