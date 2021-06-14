/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Towny mission admin root.
 */
public class TownyMissionAdminRoot extends TownyMissionAdminCommand {

    /**
     * The Commands.
     */
    Map<String, TownyMissionAdminCommand> commands;

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminRoot(TownyMission instance) {
        super(instance);
        commands = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            // This means the input is /tms admin, which is false
            Util.sendMsg(sender, instance.getLangEntry("universal.onCommandFormatError"));
            return false;
        }

        if (commands.containsKey(args[1])) {
            getExecutor(args[1]).onCommand(sender, command, alias, args);
        } else {
            onUnknown(sender);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>(commands.keySet());
        List<String> tabList;

        if (args.length == 2) {
            // This is only /tms admin
            tabList = arguments;
        } else if (commands.containsKey(args[1])) {
            tabList = commands.get(args[1]).onTabComplete(sender, command, alias, args);
        } else {
            tabList = null;
        }
        return tabList;
    }

    /**
     * Register admin command.
     *
     * @param name         the name
     * @param adminCommand the admin command
     */
    public void registerAdminCommand(String name, TownyMissionAdminCommand adminCommand) {
        commands.put(name, adminCommand);
    }

    /**
     * Gets executor.
     *
     * @param name the name
     * @return the executor
     */
    public TownyMissionAdminCommand getExecutor(String name) {
        return commands.getOrDefault(name, null);
    }

    /**
     * Sanity check boolean.
     *
     * @param player the player
     * @param args   the args
     * @return the boolean
     */
    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return false;
    }
}
