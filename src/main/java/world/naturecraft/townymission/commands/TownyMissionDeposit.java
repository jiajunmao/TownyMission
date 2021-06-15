/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.events.DoMissionEvent;
import world.naturecraft.townymission.api.exceptions.NoStartedException;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.json.mission.ResourceMissionJson;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.TimerService;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The type Towny mission deposit.
 */
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

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    MissionDao missionDao = MissionDao.getInstance();

                    if (!sanityCheck(player, args)) return;

                    MissionEntry resourceEntry = missionDao.getTownStartedMission(TownyUtil.residentOf(player), MissionType.RESOURCE);
                    ResourceMissionJson resourceMissionJson;

                    resourceMissionJson = (ResourceMissionJson) resourceEntry.getMissionJson();

                    if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                        int total = 0;
                        int index = 0;
                        for (ItemStack itemStack : player.getInventory().getContents()) {
                            if (itemStack != null && itemStack.getType().equals(resourceMissionJson.getType())) {
                                total += itemStack.getAmount();
                                player.getInventory().setItem(index, null);
                            }
                            index++;
                        }

                        resourceMissionJson.addContribution(player.getUniqueId().toString(), total);
                        resourceMissionJson.addCompleted(total);
                        Util.sendMsg(player, instance.getLangEntry("commands.deposit.onSuccess")
                                .replace("%number", String.valueOf(total)
                                        .replace("%type%", resourceMissionJson.getType().name().toLowerCase(Locale.ROOT))));
                    } else {
                        int number = player.getItemInHand().getAmount();
                        resourceMissionJson.addContribution(player.getUniqueId().toString(), player.getItemInHand().getAmount());
                        resourceMissionJson.addCompleted(number);
                        player.setItemInHand(null);
                        Util.sendMsg(player, instance.getLangEntry("commands.deposit.onSuccess")
                                .replace("%number", String.valueOf(number)
                                        .replace("%type%", resourceMissionJson.getType().name().toLowerCase(Locale.ROOT))));
                    }

                    try {
                        resourceEntry.setMissionJson(resourceMissionJson);

                        DoMissionEvent missionEvent = new DoMissionEvent(player, resourceEntry, true);
                        Bukkit.getPluginManager().callEvent(missionEvent);

                        if (!missionEvent.isCanceled()) {
                            missionDao.update(resourceEntry);
                        }
                    } catch (JsonProcessingException exception) {
                        exception.printStackTrace();
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
     * @param player the player
     * @param args   the args
     * @return the boolean
     */
    public boolean sanityCheck(@NotNull Player player, String[] args) {
        MissionDao missionDao = MissionDao.getInstance();
        return new SanityChecker(instance)
                .target(player)
                .hasTown()
                .hasStarted()
                .isMissionType(MissionType.RESOURCE)
                .customCheck(() -> {
                    MissionEntry resourceEntry = missionDao.getTownStartedMission(TownyUtil.residentOf(player), MissionType.RESOURCE);
                    ResourceMissionJson resourceMissionJson = (ResourceMissionJson) resourceEntry.getMissionJson();
                    if (player.getItemInHand().getType().equals(resourceMissionJson.getType())) {
                        return true;
                    } else {
                        Util.sendMsg(player, instance.getLangEntry("commands.deposit.onNotMatch"));
                        Util.sendMsg(player, instance.getLangEntry("commands.deposit.requiredItem").replace("%item%", resourceMissionJson.getType().name().toLowerCase(Locale.ROOT)));
                        Util.sendMsg(player, "&cIn-hand type: " + instance.getLangEntry("commands.deposit.inHandItem").replace("%item%", player.getItemInHand().getType().name().toLowerCase(Locale.ROOT)));
                        return false;
                    }
                })
                .customCheck(() -> {
                    MissionEntry resourceEntry = missionDao.getTownStartedMission(TownyUtil.residentOf(player), MissionType.RESOURCE);
                    try {
                        if (resourceEntry.isTimedout()) {
                            Util.sendMsg(player, instance.getLangEntry("commands.deposit.onMissionTimedOut"));
                            return false;
                        }
                    } catch (NoStartedException e) {
                        // This should not happen, if it gets to this, it must have been started
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    if (args.length == 1)
                        return true;
                    if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                        return true;
                    } else {
                        Util.sendMsg(player, instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                })
                .customCheck(() -> {
                    if (TimerService.getInstance().isInInterval(RankType.SEASON) || TimerService.getInstance().isInInterval(RankType.SPRINT)) {
                        Util.sendMsg(player, instance.getLangEntry("universal.onClickDuringRecess"));
                        return false;
                    }
                    return true;
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
