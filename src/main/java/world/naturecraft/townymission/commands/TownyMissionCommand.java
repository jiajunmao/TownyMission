/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.dao.*;
import world.naturecraft.townymission.data.db.sql.*;
import world.naturecraft.townymission.utils.Util;

import java.util.logging.Logger;

/**
 * The type Towny mission command.
 */
public abstract class TownyMissionCommand implements TabExecutor, CommandExecutor {

    /**
     * The Instance.
     */
    protected TownyMission instance;
    /**
     * The Logger.
     */
    protected Logger logger;

    /**
     * The Task dao.
     */
    protected MissionDao missionDao;
    protected MissionHistoryDao missionHistoryDao;
    protected SprintDao sprintDao;
    protected SeasonDao seasonDao;
    protected CooldownDao cooldownDao;

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionCommand(TownyMission instance) {
        this.instance = instance;
        this.logger = instance.getLogger();
        this.missionDao = new MissionDao((MissionDatabase) instance.getDb(DbType.TASK));
        this.missionHistoryDao = new MissionHistoryDao((MissionHistoryDatabase) instance.getDb(DbType.TASK_HISTORY));
        this.sprintDao = new SprintDao((SprintDatabase) instance.getDb(DbType.SPRINT));
        this.seasonDao = new SeasonDao((SeasonDatabase) instance.getDb(DbType.SEASON));
        this.cooldownDao = new CooldownDao((CooldownDatabase) instance.getDb(DbType.COOLDOWN));
    }

    /**
     * On unknown.
     *
     * @param sender the sender
     */
    public void onUnknown(CommandSender sender) {
        Util.sendMsg(sender, "&c The command you are looking for does not exist");
    }

    /**
     * On no permission.
     *
     * @param sender the sender
     */
    public void onNoPermission(CommandSender sender) {
        Util.sendMsg(sender, "&c You do not have the permission to execute this command");
    }
}
