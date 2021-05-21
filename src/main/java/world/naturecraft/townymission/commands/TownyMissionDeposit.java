/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.ResourceJson;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class TownyMissionDeposit extends TownyMissionCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionDeposit(TownyMission instance) {
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
        // /tms deposit
        // /tms deposit all
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean sane = sanityCheck(player, args);

            if (sane) {
                TaskEntry resourceEntry = taskDao.getTownTask(TownyUtil.residentOf(player), MissionType.RESOURCE);
                ResourceJson resourceJson;
                try {
                    resourceJson = (ResourceJson) resourceEntry.getMissionJson();

                    if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                        int total = 0;
                        int index = 0;
                        for (ItemStack itemStack : player.getInventory().getContents()) {
                            if (itemStack != null && itemStack.getType().equals(resourceJson.getType())) {
                                total += itemStack.getAmount();
                                player.getInventory().setItem(index, null);
                            }
                            index++;
                        }

                        resourceJson.addContribution(player.getUniqueId().toString(), total);
                    } else {
                        resourceJson.addContribution(player.getUniqueId().toString(), player.getItemInHand().getAmount());
                        player.setItemInHand(null);
                    }
                    resourceEntry.setMissionJson(resourceJson);
                    taskDao.update(resourceEntry);

                } catch (JsonProcessingException exception) {
                    exception.printStackTrace();
                    Util.sendMsg(player, "Something went wrong during depositing");
                    return false;
                }

                return true;
            } else {
                Util.sendMsg(player, "Something went wrong during depositing");
                logger.severe("TownyMission deposit failed sanity check");
            }
        }
        return true;
    }

    public boolean sanityCheck(Player player, String[] args) {
        return new SanityChecker(instance)
                .target(player)
                .hasTown()
                .hasStarted()
                .isMissionType(MissionType.RESOURCE)
                .customCheck(() -> {
                    TaskEntry resourceEntry = taskDao.getTownTask(TownyUtil.residentOf(player), MissionType.RESOURCE);
                    return resourceEntry != null;
                })
                .customCheck(() -> {
                    TaskEntry resourceEntry = taskDao.getTownTask(TownyUtil.residentOf(player), MissionType.RESOURCE);
                    ResourceJson resourceJson = (ResourceJson) resourceEntry.getMissionJson();
                    return player.getItemInHand().getType().equals(resourceJson.getType());
                })
                .customCheck(() -> {
                    if (args.length == 1)
                        return true;
                    return args.length == 2 && args[1].equalsIgnoreCase("all");
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
            tabList.add("all");
        }
        return tabList;
    }
}
