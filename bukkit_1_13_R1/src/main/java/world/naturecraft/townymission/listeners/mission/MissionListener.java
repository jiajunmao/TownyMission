/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners.mission;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.api.exceptions.PMCReceiveException;
import world.naturecraft.townymission.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.components.entity.PluginMessage;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.dao.MissionCacheDao;
import world.naturecraft.townymission.listeners.TownyMissionListener;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.services.PluginMessagingService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The type Mission listener.
 */
public abstract class MissionListener extends TownyMissionListener {

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public MissionListener(TownyMissionBukkit instance) {
        super(instance);
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
            instance.getLogger().warning("Main towny server, directing storage");
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
            instance.getLogger().warning("Not main towny server, passing PMC");
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    PluginMessage request = new PluginMessage()
                            .channel("mission:request")
                            .messageUUID(UUID.randomUUID())
                            .dataSize(4)
                            .data(new String[]{"doMission", player.getUniqueId().toString(), missionType.name(), String.valueOf(amount)});

                    // Check for reply and timeout to determine mission cache
                    try {
                        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request, 5, TimeUnit.SECONDS);
                        if (response.getData()[0].equalsIgnoreCase("true")) {
                            throw new PMCReceiveException("Something wrong when receiving and PMC message");
                        }
                    } catch (TimeoutException | InterruptedException | ExecutionException | PMCReceiveException e) {
                        // This means no response is received, or something went wrong, we need to cache
                        MissionCacheEntry missionCacheEntry = new MissionCacheEntry(UUID.randomUUID(),
                                player.getUniqueId(),
                                missionType,
                                amount);

                        MissionCacheDao.getInstance().add(missionCacheEntry);
                    }
                }
            };

            r.runTaskAsynchronously(instance);
        }
    }
}