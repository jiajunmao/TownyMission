/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;

import java.util.logging.Logger;

/**
 * The type Towny mission listener.
 */
public abstract class TownyMissionListener implements Listener {

    /**
     * The Instance.
     */
    protected TownyMissionBukkit instance;

    /**
     * The Plugin manager.
     */
    protected PluginManager pluginManager;

    /**
     * The Logger.
     */
    protected Logger logger;

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public TownyMissionListener(TownyMissionBukkit instance) {
        this.instance = instance;

        pluginManager = Bukkit.getPluginManager();
        logger = instance.getLogger();
    }

    /**
     * Run task asynchronously.
     *
     * @param r the r
     */
    protected void runTaskAsynchronously(BukkitRunnable r) {
        r.runTaskAsynchronously(instance);
    }
}
