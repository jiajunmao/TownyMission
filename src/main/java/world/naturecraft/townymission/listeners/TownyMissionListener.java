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
import world.naturecraft.townymission.data.dao.CooldownDao;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.data.dao.MissionHistoryDao;
import world.naturecraft.townymission.data.db.sql.CooldownDatabase;
import world.naturecraft.townymission.data.db.sql.SprintDatabase;
import world.naturecraft.townymission.data.db.sql.MissionDatabase;
import world.naturecraft.townymission.data.db.sql.MissionHistoryDatabase;

import java.util.logging.Logger;

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
    protected MissionDao missionDao;

    protected MissionHistoryDao missionHistoryDao;

    protected SprintDao sprintDao;

    protected CooldownDao cooldownDao;

    /**
     * The Plugin manager.
     */
    protected PluginManager pluginManager;

    protected Logger logger;

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public TownyMissionListener(TownyMission instance) {
        this.instance = instance;

        missionDao = new MissionDao((MissionDatabase) instance.getDb(DbType.MISSION));
        missionHistoryDao = new MissionHistoryDao((MissionHistoryDatabase) instance.getDb(DbType.MISSION_HISTORY));
        sprintDao = new SprintDao((SprintDatabase) instance.getDb(DbType.SPRINT));
        cooldownDao = new CooldownDao((CooldownDatabase) instance.getDb(DbType.COOLDOWN));

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
