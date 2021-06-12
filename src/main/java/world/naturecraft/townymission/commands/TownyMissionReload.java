/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.Util;

import javax.swing.*;
import java.util.List;
import java.util.Locale;

public class TownyMissionReload extends TownyMissionCommand {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionReload(TownyMission instance) {
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
                .customCheck(() -> new SanityChecker(instance).target(player).hasPermission("tms.admin").check()
                        || new SanityChecker(instance).target(player).hasPermission("tms.command.reload").check())
                .customCheck(() -> {
                    if (args.length == 1 && args[0].equalsIgnoreCase("reload"))
                        return true;

                    Util.sendMsg(player, instance.getLangEntry("universal.onCommandFormatError"));
                    return false;
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
        // /tms reload
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!sanityCheck(player, args)) return false;

            instance.reloadConfigs();
            Util.sendMsg(player, instance.getLangEntry("commands.reload.onSuccess"));
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
