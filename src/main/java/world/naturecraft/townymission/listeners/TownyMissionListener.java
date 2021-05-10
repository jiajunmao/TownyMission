/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.listeners;

import org.bukkit.event.Listener;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.dao.TaskDao;
import world.naturecraft.townymission.db.sql.TaskDatabase;

public abstract class TownyMissionListener implements Listener {

    protected TownyMission instance;
    protected TaskDao taskDao;

    public TownyMissionListener(TownyMission instance) {
        this.instance = instance;
        taskDao = new TaskDao((TaskDatabase) instance.getDb(DbType.TASK));
    }
}
