/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.services;

import com.palmergames.bukkit.towny.object.Town;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.services.MissionBukkitService;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.bungee.services.MissionBungeeService;
import world.naturecraft.townymission.bungee.utils.BungeeChecker;
import world.naturecraft.townymission.core.components.entity.*;
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
            if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
                singleton = MissionBukkitService.getInstance();
            } else {
                singleton = MissionBungeeService.getInstance();
            }
        }
        return singleton;
    }

    public abstract boolean canStartMission(UUID playerUUID);

    public abstract boolean canAbortMission(UUID playerUUID, MissionEntry entry);

    /**
     * Has started boolean.
     *
     * @param town the town
     * @return the boolean
     */
    public boolean hasStarted(Town town) {
        return MissionDao.getInstance().getStartedMission(town) == null;
    }

    public boolean hasStarted(String townUUID) {
        return MissionDao.getInstance().getStartedMission(UUID.fromString(townUUID)) != null;
    }

    /**
     * Start mission.
     *
     * @param playerUUID the player
     * @param choice the choice
     * @return the boolean
     */
    public boolean startMission(UUID playerUUID, int choice) {
        if (!canStartMission(playerUUID))
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
     * @param playerUUID the player
     * @param entry  the entry
     */
    public void abortMission(UUID playerUUID, MissionEntry entry) {
        if (!canAbortMission(playerUUID, entry))
            return;

        MissionDao.getInstance().remove(entry);
        CooldownService.getInstance().startCooldown(entry.getTown(), Util.minuteToMs(instance.getInstanceConfig().getInt("mission.cooldown")));
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
        CooldownService.getInstance().startCooldown(entry.getTown(), Util.minuteToMs(instance.getInstanceConfig().getInt("mission.cooldown")));
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
                MissionService.getInstance().completeMission(entry.getTown().getMayor().getPlayer(), entry);
            }

            // Remove the entry from MissionStorage
            MissionDao.getInstance().remove(entry);
        }
    }
}
