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
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.Util;

import java.util.Date;
import java.util.List;

/**
 * The type Towny mission admin start season.
 */
public class TownyMissionAdminStartSeason extends TownyMissionAdminCommand {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminStartSeason(TownyMission instance) {
        super(instance);
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
        return new SanityChecker(instance).target(player)
                .customCheck(() -> {
                    return new SanityChecker(instance).target(player).hasPermission("townymission.admin").check()
                            || new SanityChecker(instance).target(player).hasPermission("townymission.commands.admin.reload").check();
                })
                .customCheck(() -> {
                    // /tms admin startSeason
                    return (args.length == 2 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("startSeason"));
                }).check();
    }

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!sanityCheck(player, args)) return false;

            if (instance.getStatsConfig().getLong("season.startedTime") == -1) {
                Date date = new Date();
                instance.getStatsConfig().set("season.startedTime", date.getTime());
                instance.getStatsConfig().save();
                Util.sendMsg(player, instance.getLangEntry("commands.startSeason.onSuccess"));
                return true;
            } else {
                Util.sendMsg(player, instance.getLangEntry("commands.startSeason.onAlreadyStarted"));
                return false;
            }
        }

        return true;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
