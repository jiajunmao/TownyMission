/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners;

import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.data.dao.MissionDao;

/**
 * The type Town fall listener.
 */
public class TownFallListener extends TownyMissionListener {
    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public TownFallListener(TownyMissionBukkit instance) {
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
                // Remove all
                for (MissionEntry entry : MissionDao.getInstance().getTownMissions(town.getUUID())) {
                    MissionDao.getInstance().remove(entry);
                }
            }
        };

        runTaskAsynchronously(r);
    }
}
