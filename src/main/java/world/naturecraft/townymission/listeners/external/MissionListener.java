/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners.external;

import com.Zrips.CMI.events.CMIUserBalanceChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.event.TownClaimEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import teozfrank.ultimatevotes.events.VoteRewardEvent;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.events.DoMissionEvent;
import world.naturecraft.townymission.components.containers.json.*;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.listeners.TownyMissionListener;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;

public class MissionListener extends TownyMissionListener {

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public MissionListener(TownyMission instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVoteReceived(VoteRewardEvent e) {
        Player player = e.getPlayer();

        SanityChecker sanityChecker = new SanityChecker(instance).target(player)
                .hasTown()
                .hasStarted()
                .isMissionType(MissionType.VOTE);

        doLogic(sanityChecker, MissionType.VOTE, player, e.getUnclaimedCount());
    }

    @EventHandler
    public void onMoneyReceive(CMIUserBalanceChangeEvent event) {
        Player player = event.getUser().getPlayer();
        SanityChecker checker = new SanityChecker(instance).target(player)
            .hasTown()
            .hasStarted()
            .isMissionType(MissionType.MONEY)
            .customCheck(() -> event.getSource() == null)
            .customCheck(() -> (event.getTo() - event.getFrom() > 0));


        doLogic(checker, MissionType.MONEY, event.getUser().getPlayer(), (int) (event.getTo() - event.getFrom()));
    }

    @EventHandler
    public void onTownExpansion(TownClaimEvent e) {
        SanityChecker checker = new SanityChecker(instance).target(e.getResident().getPlayer())
            .hasTown()
            .hasStarted()
            .isMissionType(MissionType.EXPANSION)
            .customCheck(() -> {
                try {
                    TaskEntry entry = taskDao.getTownTask(e.getTownBlock().getTown(), MissionType.EXPANSION);
                    return entry.getTown().equals(e.getTownBlock().getTown());
                } catch (NotRegisteredException notRegisteredException) {
                    return false;
                }

            });

        doLogic(checker, MissionType.EXPANSION, e.getResident().getPlayer(), 1);
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        LivingEntity dead = e.getEntity();
        Player killer = e.getEntity().getKiller();

        if (killer != null) {

            SanityChecker checker =  new SanityChecker(instance).target(killer)
                    .hasTown()
                    .hasStarted()
                    .isMissionType(MissionType.MOB)
                    .customCheck(() -> {
                        TaskEntry taskEntry = taskDao.getTownTask(TownyUtil.residentOf(killer), MissionType.MOB);
                        MobJson mobJson = (MobJson) taskEntry.getMissionJson();
                        return mobJson.getEntityType().equals(dead.getType());
                    });

            doLogic(checker, MissionType.MOB, killer, 1);
        }
    }

    public void doLogic(SanityChecker sanityChecker, MissionType missionType, Player player, int amount) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (sanityChecker.check()) {
                    Town town = TownyUtil.residentOf(player);
                    TaskEntry taskEntry = taskDao.getTownTask(town, missionType);
                    MissionJson json = taskEntry.getMissionJson();
                    json.setCompleted(json.getCompleted() + amount);
                    try {
                        taskEntry.setMissionJson(json);
                    } catch (JsonProcessingException exception) {
                        exception.printStackTrace();
                        return;
                    }

                    DoMissionEvent missionEvent = new DoMissionEvent(player, taskEntry);
                    pluginManager.callEvent(missionEvent);
                    try {
                        if (!missionEvent.isCanceled()) {
                            taskDao.update(taskEntry);
                        }
                    } catch (JsonProcessingException exception) {
                        exception.printStackTrace();
                        return;
                    }

                }
            }
        };

        runTaskAsynchronously(r);
    }
}