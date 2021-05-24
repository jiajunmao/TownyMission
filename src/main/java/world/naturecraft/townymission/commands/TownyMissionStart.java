/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Towny mission start.
 */
public class TownyMissionStart extends TownyMissionCommand {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionStart(TownyMission instance) {
        super(instance);
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
        // /townymission start <number>
        if (sender instanceof Player) {
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = (Player) sender;
                    if (sanityCheck(player, args)) {
                        Town town = TownyUtil.residentOf(player);
                        List<TaskEntry> taskEntries = taskDao.getTownTasks(town);
                        int missionIdx = Integer.parseInt(args[1]);

                        TaskEntry entry = taskEntries.get(missionIdx - 1);
                        entry.setStartedTime(Util.currentTime());
                        entry.setStartedPlayer(player);

                        try {
                            taskDao.update(entry);
                            Util.sendMsg(sender, "&f You have started " + entry.getMissionType() + " " + entry.getDisplayLine());
                        } catch (JsonProcessingException e) {
                            logger.severe("Error while parsing Json " + entry.getMissionJson());
                            e.printStackTrace();
                            // I want to want to write a comment
                        }
                    }
                }
            };

            r.runTaskAsynchronously(instance);
        }

        return true;
    }

    /**
     * Sanity check boolean.
     *
     * @param player the sender
     * @param args   the args
     * @return the boolean
     */
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        // /tm start <num>
        return new SanityChecker(instance).target(player)
            .hasTown()
            .hasPermission("townymission.player")
            .customCheck(() -> {
                if (args.length == 1 || (Integer.parseInt(args[1]) <= 15 && Integer.parseInt(args[1]) >= 1)) {
                    Util.sendMsg(player, Util.getLangEntry("universal.onCommandFormatError", instance));
                    return false;
                }
                return true;
            }).customCheck(() -> {
                Town town = TownyUtil.residentOf(player);
                if (taskDao.getStartedMission(town) == null) {
                    return true;
                } else {
                    Util.sendMsg(player, Util.getLangEntry("commands.start.onAlreadyStarted", instance));
                    return false;
                }
            }).check();
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
        List<String> tabList = new ArrayList<>();
        if (args.length == 2) {
            for (int i = 1; i <= 15; i++) {
                tabList.add(String.valueOf(i));
            }
        }
        return tabList;
    }
}
