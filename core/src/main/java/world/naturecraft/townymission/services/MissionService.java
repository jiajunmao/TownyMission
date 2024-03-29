/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.naturelib.InstanceType;
import world.naturecraft.naturelib.utils.EntryFilter;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.events.MissionCompleteEvent;
import world.naturecraft.townymission.components.entity.*;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.mission.MissionJson;
import world.naturecraft.townymission.components.json.mission.MoneyMissionJson;
import world.naturecraft.townymission.components.json.mission.ResourceMissionJson;
import world.naturecraft.townymission.components.json.reward.MoneyRewardJson;
import world.naturecraft.townymission.components.json.reward.ResourceRewardJson;
import world.naturecraft.townymission.data.dao.*;
import world.naturecraft.townymission.services.core.CooldownService;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
            if (InstanceType.isBukkit()) {
                String packageName = MissionService.class.getPackage().getName();
                try {
                    singleton = (MissionService) Class.forName(packageName + "." + "MissionBukkitService").newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
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
        return MissionDao.getInstance().getStartedMissions(townUUID).size() != 0;
    }

    /**
     * Start mission.
     *
     * @param playerUUID the player
     * @param missionIdx the choice
     * @return the boolean
     */
    public boolean startMission(UUID playerUUID, int missionIdx) {
        if (!canStartMission(playerUUID))
            return false;

        UUID townUUID = TownyService.getInstance().residentOf(playerUUID);

        List<MissionEntry> taskEntries = MissionDao.getInstance().getEntries(new EntryFilter<MissionEntry>() {
            @Override
            public boolean include(MissionEntry missionEntry) {
                return missionEntry.getTownUUID().equals(townUUID) && missionEntry.getNumMission() == missionIdx;
            }
        });

        MissionEntry entry = taskEntries.get(0);

        entry.setStartedTime(new Date().getTime());
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
     * @param player the player
     * @param entry  the entry
     * @param force  when this is set to true, the function will not check whether player is eligible for aborting the mission. The player UUID can be null.
     */
    public void abortMission(UUID player, MissionEntry entry, boolean force) {
        if (!force) {
            if (!canAbortMission(player, entry))
                return;
        }

        // Notify the town on abort mission
        for (UUID uuid : TownyService.getInstance().getResidents(entry.getTownUUID())) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (!uuid.equals(player) && !force && offlinePlayer.isOnline()) {
                Player p = (Player) offlinePlayer;
                ChatService.getInstance().sendMsg(uuid, instance.getLangEntry("commands.abort.onSuccessBroadcast").replace("%player%", p.getName()).replace("%mission%", entry.getDisplayLine()));
            }
        }

        giveBack(entry);
        MissionDao.getInstance().remove(entry);
        CooldownService.getInstance().startCooldown(entry.getTownUUID(), entry.getNumMission(), TimeUnit.MILLISECONDS.convert(instance.getInstanceConfig().getInt("mission.cooldown"), TimeUnit.MINUTES));
    }

    private void giveBack(MissionEntry entry) {
        if (entry.getMissionType().equals(MissionType.MONEY)) {
            MoneyMissionJson moneyMissionJson = (MoneyMissionJson) entry.getMissionJson();
            if (moneyMissionJson.isReturnable()) {
                Map<String, Integer> contributions = entry.getMissionJson().getContributions();
                for (String playerUUID : contributions.keySet()) {
                    MoneyRewardJson moneyRewardJson = new MoneyRewardJson(contributions.get(playerUUID));
                    ClaimEntry claimEntry = new ClaimEntry(UUID.randomUUID(), UUID.fromString(playerUUID), RewardType.MONEY, moneyRewardJson, instance.getStatsConfig().getInt("season.current"), instance.getStatsConfig().getInt("sprint.current"));
                    ClaimDao.getInstance().addAndMerge(claimEntry);
                }
            }
        }

        if (entry.getMissionType().equals(MissionType.RESOURCE)) {
            ResourceMissionJson resourceMissionJson = (ResourceMissionJson) entry.getMissionJson();
            if (resourceMissionJson.isReturnable()) {
                Map<String, Integer> contributions = entry.getMissionJson().getContributions();
                for (String playerUUID : contributions.keySet()) {
                    int amount = contributions.get(playerUUID);
                    ResourceRewardJson resourceRewardJson = new ResourceRewardJson(resourceMissionJson.isMi(), resourceMissionJson.getType(), resourceMissionJson.getMiID(), amount);
                    ClaimEntry claimEntry = new ClaimEntry(UUID.randomUUID(), UUID.fromString(playerUUID), RewardType.RESOURCE, resourceRewardJson, instance.getStatsConfig().getInt("season.current"), instance.getStatsConfig().getInt("sprint.current"));
                    ClaimDao.getInstance().addAndMerge(claimEntry);
                }
            }
        }
    }

    /**
     * Complete mission.
     *
     * @param entry the entry
     */
    public void completeMission(OfflinePlayer player, MissionEntry entry) {
        if (entry.isTimedout() && !entry.isCompleted()) return;

        giveBack(entry);

        // Send out completion event
        MissionCompleteEvent completeEvent = new MissionCompleteEvent(player, new Date().getTime(), entry);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(completeEvent);
            }
        };
        runnable.runTask(TownyMissionInstance.getInstance());


        // Send out mission completion notification
        if (player != null) {
            List<UUID> residentList = TownyService.getInstance().getResidents(entry.getTownUUID());
            for (UUID uuid : residentList) {
                if (uuid != player.getUniqueId() && Bukkit.getOfflinePlayer(uuid).isOnline()) {
                    ChatService.getInstance().sendMsg(uuid, instance.getLangEntry("commands.complete.onSuccessBroadcast").replace("%player%", player.getName()).replace("%mission%", entry.getDisplayLine()));
                }
            }
        }

        // Database jobs
        MissionDao.getInstance().remove(entry);
        MissionHistoryEntry missionHistoryEntry = new MissionHistoryEntry(entry, new Date().getTime());
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

        CooldownService.getInstance().startCooldown(entry.getTownUUID(), entry.getNumMission(), TimeUnit.MILLISECONDS.convert(instance.getInstanceConfig().getInt("mission.cooldown"), TimeUnit.MINUTES));
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

    public abstract void doMission(UUID townUUID, UUID playerUUID, MissionType missionType, int amount);

    /**
     * Sprint end clean up.
     */
    public void sprintEndCleanUp() {
        List<MissionEntry> entryList = MissionDao.getInstance().getEntries();
        for (MissionEntry entry : entryList) {
            if (entry.isStarted() && entry.isCompleted()) {
                // If it is already completed, but unmoved, move to MissionHistory, and give the reward
                MissionService.getInstance().completeMission(null, entry);
            }

            // Remove the entry from MissionStorage
            MissionDao.getInstance().remove(entry);
        }
    }
}
