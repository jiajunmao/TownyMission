/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands.admin.season;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.naturelib.exceptions.ConfigSavingException;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionAdminCommand;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.Util;

import java.util.Date;
import java.util.List;

/**
 * The type Towny mission admin start season.
 */
public class TownyMissionAdminSeasonStart extends TownyMissionAdminCommand {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminSeasonStart(TownyMissionBukkit instance) {
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
    public boolean playerSanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .hasPermission(new String[]{"townymission.admin.season.start", "townymission.admin"}).check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        return new BukkitChecker(instance)
                .customCheck(() -> {
                    // /tmsa season start
                    if (args.length != 2 || !args[0].equalsIgnoreCase("season") || !args[1].equalsIgnoreCase("start")) {
                        //System.out.println("It's ME!");
                        commandSender.sendMessage(instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
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
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (!sanityCheck(sender, args)) return;

                if (instance.getStatsConfig().getLong("season.startedTime") == -1) {
                    Date date = new Date();
                    instance.getStatsConfig().set("season.startedTime", date.getTime());
                    try {
                        instance.getStatsConfig().save();
                        sender.sendMessage(instance.getLangEntry("commands.startSeason.onSuccess"));
                    } catch (ConfigSavingException e) {
                        sender.sendMessage(instance.getLangEntry("commands.startSeason.onFailure"));
                        e.printStackTrace();
                    }
                } else if (instance.getStatsConfig().getLong("season.pausedTime") != -1) {
                    // This means that the season is currently paused
                    long pausedTime = instance.getStatsConfig().getLong("season.pausedTime");
                    long timeNow = new Date().getTime();
                    long elapsedTime = timeNow - pausedTime;
                    long offsetStartedTime = instance.getStatsConfig().getLong("season.startedTime") - elapsedTime;
                    instance.getStatsConfig().set("season.startedTime", offsetStartedTime);
                    instance.getStatsConfig().set("season.pausedTime", -1);
                    try {
                        instance.getStatsConfig().save();
                        sender.sendMessage(instance.getLangEntry("commands.startSeason.onUnpause").replace("%time%", Util.formatMilliseconds(elapsedTime)));
                    } catch (ConfigSavingException e) {
                        sender.sendMessage(instance.getLangEntry("commands.startSeason.onFailure"));
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(instance.getLangEntry("commands.startSeason.onAlreadyStarted"));
                }
            }
        };

        r.runTaskAsynchronously(instance);
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
