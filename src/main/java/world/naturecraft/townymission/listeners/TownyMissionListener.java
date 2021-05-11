/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners;

import org.bukkit.event.Listener;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.dao.TaskDao;
import world.naturecraft.townymission.db.sql.TaskDatabase;

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

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public TownyMissionListener(TownyMission instance) {
        this.instance = instance;
        taskDao = new TaskDao((TaskDatabase) instance.getDb(DbType.TASK));
    }
}
