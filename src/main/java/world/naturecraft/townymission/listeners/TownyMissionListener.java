/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.dao.TaskDao;
import world.naturecraft.townymission.dao.TaskHistoryDao;
import world.naturecraft.townymission.db.sql.TaskDatabase;
import world.naturecraft.townymission.db.sql.TaskHistoryDatabase;

/**
 * The type Towny mission listener.
 */
public abstract class TownyMissionListener implements Listener {

    /**
     * The Instance.
     */
    protected TownyMission instance;
    /**
     * The Task dao.
     */
    protected TaskDao taskDao;

    protected TaskHistoryDao taskHistoryDao;

    /**
     * The Plugin manager.
     */
    protected PluginManager pluginManager;

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public TownyMissionListener(TownyMission instance) {
        this.instance = instance;
        taskDao = new TaskDao((TaskDatabase) instance.getDb(DbType.TASK));
        taskHistoryDao = new TaskHistoryDao((TaskHistoryDatabase) instance.getDb(DbType.TASK_HISTORY));
        pluginManager = Bukkit.getPluginManager();
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
