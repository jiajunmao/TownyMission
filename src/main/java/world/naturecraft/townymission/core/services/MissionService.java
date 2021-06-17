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
import world.naturecraft.townymission.bungee.TownyMissionBungee;
import world.naturecraft.townymission.core.components.entity.*;
import world.naturecraft.townymission.core.components.json.mission.MissionJson;
import world.naturecraft.townymission.core.data.dao.*;
import world.naturecraft.townymission.core.utils.EntryFilter;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.utils.Util;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * The type Mission service.
 */
public class MissionService extends TownyMissionService {

    private static MissionService singleton;

    public MissionService(TownyMissionInstance instance) {
        super(instance);
    }
    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionService getInstance() {
        if (singleton == null) {
            singleton = new MissionService(TownyMissionInstance.getInstance());
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
        if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
            TownyMissionBukkit townyMissionBukkit = TownyMissionInstance.getInstance();
            return new BukkitChecker(townyMissionBukkit).target(player)
                    .hasTown()
                    .hasPermission("townymission.player")
                    .customCheck(() -> {
                        Town town = TownyUtil.residentOf(player);

                        if (MissionDao.getInstance().getStartedMission(town) == null) {
                            return true;
                        } else {
                            BukkitUtil.sendMsg(player, instance.getLangEntry("commands.start.onAlreadyStarted"));
                            return false;
                        }
                    }).check();
        } else {
            // TODO: Async this!!!!
            boolean overallResult = true;
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getUniqueId());
            // Send the message to the server
            UUID uuid = UUID.randomUUID();
            // Check whether has town
            PluginMessage request = new PluginMessage(
                    proxiedPlayer.getUniqueId(),
                    "sanitycheck:request",
                    uuid,
                    1,
                    new String[]{"hasTown"}
            );

            // Registering CompletableFuture to get response
            PluginMessage response = PluginMessagingService.getInstance().sendAndWaitForResponse(request);
            // We know that data[0] should contain the boolean response
            if (!Boolean.parseBoolean(response.getData()[0])) {
                return false;
            }

            uuid = UUID.randomUUID();
            // Check whether has permission
            request = new PluginMessage(
                    proxiedPlayer.getUniqueId(),
                    "sanitycheck:request",
                    uuid,
                    1,
                    new String[]{"hasPermission", "townymission.player"}
            );

            response = PluginMessagingService.getInstance().sendAndWaitForResponse(request);
            if (!Boolean.parseBoolean(response.getData()[0])) {
                return false;
            }

            uuid = UUID.randomUUID();
            // Check whether has permission
            request = new PluginMessage(
                    proxiedPlayer.getUniqueId(),
                    "data:request",
                    uuid,
                    1,
                    new String[]{"getTownUUID"}
            );

            response = PluginMessagingService.getInstance().sendAndWaitForResponse(request);
            String townUUID = request.getData()[0];

            if (MissionDao.getInstance().getStartedMission(UUID.fromString(townUUID)) == null) {
                return true;
            } else {
                BukkitUtil.sendMsg(player, instance.getLangEntry("commands.start.onAlreadyStarted"));
                return false;
            }
        }
    }

    private boolean canAbortMission(Player player, MissionEntry entry) {
        if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
            TownyMissionBukkit townyMissionBukkit = TownyMissionInstance.getInstance();
            BukkitChecker checker = new BukkitChecker(townyMissionBukkit).target(player)
                    .hasTown()
                    .hasStarted()
                    .hasPermission("townymission.player")
                    .customCheck(() -> {
                        if (TownyUtil.mayorOf(player) != null)
                            return true;

                        return entry.getStartedPlayer().equals(player);
                    });

            return checker.check();
        } else {
            //TODO: fix this
            return false;
        }
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
