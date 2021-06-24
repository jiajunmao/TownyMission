/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.listeners.external;

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
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.events.DoMissionEvent;
import world.naturecraft.townymission.bukkit.listeners.TownyMissionListener;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.core.components.entity.MissionEntry;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.MissionJson;
import world.naturecraft.townymission.core.components.json.mission.MobMissionJson;
import world.naturecraft.townymission.core.data.dao.MissionDao;
import world.naturecraft.townymission.core.services.MissionService;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.Locale;
import java.util.UUID;

/**
 * The type Mission listener.
 */
public class MissionListener extends TownyMissionListener {

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public MissionListener(TownyMissionBukkit instance) {
        super(instance);
    }

    /**
     * On vote received.
     *
     * @param e the e
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVoteReceived(VoteRewardEvent e) {
        Player player = e.getPlayer();

        BukkitChecker bukkitChecker = new BukkitChecker(instance).target(player)
                .hasTown()
                .hasStarted()
                .isMissionType(MissionType.VOTE);

        doLogic(bukkitChecker, MissionType.VOTE, player, e.getUnclaimedCount());
    }

    /**
     * On money receive.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMoneyReceive(CMIUserBalanceChangeEvent event) {
        Player player = event.getUser().getPlayer();
        BukkitChecker checker = new BukkitChecker(instance).target(player).silent(true)
                .hasTown()
                .hasStarted()
                .isMissionType(MissionType.MONEY)
                .customCheck(() -> event.getSource() == null)
                .customCheck(() -> (event.getTo() - event.getFrom() > 0));

        doLogic(checker, MissionType.MONEY, event.getUser().getPlayer(), (int) (event.getTo() - event.getFrom()));
    }

    /**
     * On town expansion.
     *
     * @param e the e
     */
    @EventHandler
    public void onTownExpansion(TownClaimEvent e) {
        BukkitChecker checker = new BukkitChecker(instance).target(e.getResident().getPlayer())
                .hasTown()
                .hasStarted()
                .isMissionType(MissionType.EXPANSION)
                .customCheck(() -> {
                    try {
                        MissionEntry entry = MissionDao.getInstance().getTownStartedMission(e.getTownBlock().getTown().getUUID(), MissionType.EXPANSION);
                        return entry.getTownUUID().equals(e.getTownBlock().getTown().getUUID());
                    } catch (NotRegisteredException notRegisteredException) {
                        return false;
                    }

                });

        doLogic(checker, MissionType.EXPANSION, e.getResident().getPlayer(), 1);
    }

    /**
     * On mob kill.
     *
     * @param e the e
     */
    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        LivingEntity dead = e.getEntity();
        Player killer = e.getEntity().getKiller();

        if (killer != null) {

            BukkitChecker checker = new BukkitChecker(instance).target(killer)
                    .hasTown()
                    .hasStarted()
                    .isMissionType(MissionType.MOB)
                    .customCheck(() -> {
                        MissionEntry taskEntry = MissionDao.getInstance().getTownStartedMission(TownyUtil.residentOf(killer).getUUID(), MissionType.MOB);
                        MobMissionJson mobMissionJson = (MobMissionJson) taskEntry.getMissionJson();
                        return mobMissionJson.getEntityType().equals(dead.getType());
                    });

            doLogic(checker, MissionType.MOB, killer, 1);
        }
    }

    /**
     * Do logic.
     *
     * @param bukkitChecker the sanity checker
     * @param missionType   the mission type
     * @param player        the player
     * @param amount        the amount
     */
    public void doLogic(BukkitChecker bukkitChecker, MissionType missionType, Player player, int amount) {
        // This is where main server and non-main server differs
        // If it is main server, directly interact with the DAOs
        // If it is not the main server, send the event to the main server through PMC

        if (!instance.getConfig().getBoolean("bungeecord.enable") || (instance.getConfig().getBoolean("bungeecord.enable") && instance.getConfig().getBoolean("bungeecord.main-server"))) {
            // This means either the bungeecord is not enabled
            //  or bungeecord is enabled and this is main server
            //  directly interact with the DAOs
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    if (bukkitChecker.check()) {
                        Town town = TownyUtil.residentOf(player);
                        MissionService.getInstance().doMission(town.getUUID(), player.getUniqueId(), missionType, amount);
                    }
                }
            };

            runTaskAsynchronously(r);
        } else if (instance.getConfig().getBoolean("bungeecord.enable") && !instance.getConfig().getBoolean("bungeecord.main-server")) {
            // This means that bungeecord is enabled, but this is not the main server
            //    send the package to the main server instead

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    PluginMessage request = new PluginMessage()
                            .channel("mission:request")
                            .messageUUID(UUID.randomUUID())
                            .dataSize(4)
                            .data(new String[]{"doMission", player.getUniqueId().toString(), missionType.name(), String.valueOf(amount)});

                    // No need for reply
                    PluginMessagingService.getInstance().send(request);
                }
            };

            r.runTaskAsynchronously(instance);
        }
    }
}