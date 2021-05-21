/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.events.DoMissionEvent;
import world.naturecraft.townymission.components.containers.json.MissionJson;
import world.naturecraft.townymission.components.containers.json.MobJson;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.listeners.TownyMissionListener;
import world.naturecraft.townymission.utils.MissionJsonFactory;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;

public class MobListener extends TownyMissionListener {

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public MobListener(TownyMission instance) {
        super(instance);
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
                    if (!missionEvent.isCanceled()) {
                        taskDao.update(taskEntry);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        LivingEntity dead = e.getEntity();
        Player killer = e.getEntity().getKiller();

        if (killer != null) {
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    boolean sane = new SanityChecker(instance).target(killer)
                        .hasTown()
                        .hasStarted()
                        .isMissionType(MissionType.MOB)
                        .customCheck(() -> {
                            TaskEntry taskEntry = taskDao.getTownTask(TownyUtil.residentOf(killer), MissionType.MOB);
                            MobJson mobJson = (MobJson) taskEntry.getMissionJson();
                            return mobJson.getEntityType().equals(dead.getType());
                        }).check();

                    if (sane) {
                        TaskEntry taskEntry;
                        try {
                            taskEntry = taskDao.getStartedMission(TownyUtil.residentOf(killer));
                            MobJson mobJson = (MobJson) taskEntry.getMissionJson();
                            mobJson.setCompleted(mobJson.getCompleted() + 1);
                            taskEntry.setMissionJson(mobJson);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            return;
                        }

                        DoMissionEvent missionEvent = new DoMissionEvent(killer, taskEntry);
                        pluginManager.callEvent(missionEvent);
                        if (!missionEvent.isCanceled()) {
                            taskDao.update(taskEntry);
                        }
                    }
                }
            };

            r.runTaskAsynchronously(instance);
        }
    }
}