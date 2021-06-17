/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;

import java.util.logging.Logger;

/**
 * The type Towny mission command.
 */
public abstract class TownyMissionCommand implements TabExecutor, CommandExecutor {

    /**
     * The Instance.
     */
    protected TownyMissionBukkit instance;
    /**
     * The Logger.
     */
    protected Logger logger;

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionCommand(TownyMissionBukkit instance) {
        this.instance = instance;
        this.logger = instance.getLogger();
    }

    /**
     * On unknown.
     *
     * @param sender the sender
     */
    public void onUnknown(CommandSender sender) {
        BukkitUtil.sendMsg(sender, "&c The command you are looking for does not exist");
    }

    /**
     * On no permission.
     *
     * @param sender the sender
     */
    public void onNoPermission(CommandSender sender) {
        BukkitUtil.sendMsg(sender, "&c You do not have the permission to execute this command");
    }

    /**
     * Sanity check boolean.
     *
     * @param player the player
     * @param args   the args
     * @return the boolean
     */
    public abstract boolean sanityCheck(@NotNull Player player, @NotNull String[] args);
}
