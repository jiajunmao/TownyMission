/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.List;

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
            Player player = (Player) sender;
            if (sanityCheck(sender, args)) {
                Town town = TownyUtil.residentOf(player);
                List<TaskEntry> taskEntries = taskDao.getTownTasks(town);
                int missionIdx = Integer.parseInt(args[1]);

                TaskEntry entry = taskEntries.get(missionIdx - 1);
                entry.setStartedTime(Util.currentTime());
                taskDao.update(entry);

                try {
                    Util.sendMsg(sender, "&f You have started " + entry.getTaskType() + " " + entry.getDisplayLine());
                } catch (JsonProcessingException e) {
                    logger.severe("Error while parsing Json " + entry.getTaskJson());
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public boolean sanityCheck(@NotNull CommandSender sender, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("townymission.player")) {
                if (Integer.parseInt(args[1]) <= 15 && Integer.parseInt(args[1]) >= 1) {
                    Town town;
                    if ((town = TownyUtil.residentOf(player)) != null) {
                        if (!taskDao.hasStartedMission(town)) {
                            return true;
                        } else {
                            Util.sendMsg(sender, "&c Your town already has a started mission!");
                            return false;
                        }
                    } else {
                        Util.sendMsg(sender, "&c You would have to be in a town to use TownyMission");
                        return false;
                    }
                } else {
                    Util.sendMsg(sender, "&c Command format error");
                    return false;
                }
            } else {
                onNoPermission(sender);
                return false;
            }
        } else {
            return true;
        }
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
