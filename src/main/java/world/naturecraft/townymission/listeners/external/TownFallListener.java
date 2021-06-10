/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners.external;

import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.MissionEntry;
import world.naturecraft.townymission.listeners.TownyMissionListener;

/**
 * The type Town fall listener.
 */
public class TownFallListener extends TownyMissionListener {
    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public TownFallListener(TownyMission instance) {
        super(instance);
    }

    /**
     * On town fall.
     *
     * @param e the e
     */
    @EventHandler
    public void onTownFall(TownRuinedEvent e) {
        Town town = e.getTown();

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                for (MissionEntry entry : missionDao.getTownMissions(town)) {
                    missionDao.remove(entry);
                }
            }
        };

        runTaskAsynchronously(r);
    }
}
