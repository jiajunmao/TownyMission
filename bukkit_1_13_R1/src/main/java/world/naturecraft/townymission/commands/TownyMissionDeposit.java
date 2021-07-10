/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.api.events.DoMissionEvent;
import world.naturecraft.townymission.api.exceptions.NoStartedException;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.entity.PluginMessage;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.json.mission.ResourceMissionJson;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.PluginMessagingService;
import world.naturecraft.townymission.services.TimerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * The type Towny mission deposit.
 */
public class TownyMissionDeposit extends TownyMissionCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionDeposit(TownyMissionBukkit instance) {
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

                    MissionEntry resourceEntry = MissionDao.getInstance().getTownStartedMission(TownyUtil.residentOf(player).getUUID(), MissionType.RESOURCE);
                    ResourceMissionJson resourceMissionJson = (ResourceMissionJson) resourceEntry.getMissionJson();

                    TownyMissionBukkit instance = TownyMissionInstance.getInstance();
                    if (instance.isMainserver()) {
                        // This means that this is the main server, directly interact with the services

                        if (!sanityCheck(player, args)) return;

                        int number;
                        if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                            number = getTotalAndSetNull(player, Material.valueOf(resourceMissionJson.getType()));
                        } else {
                            number = player.getItemInHand().getAmount();
                        }

                        resourceMissionJson.addContribution(player.getUniqueId().toString(), number);
                        resourceMissionJson.addCompleted(number);
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onSuccess")
                                .replace("%number%", String.valueOf(number)).replace("%type%", resourceMissionJson.getType().toLowerCase(Locale.ROOT)));

                        try {
                            resourceEntry.setMissionJson(resourceMissionJson);

                            DoMissionEvent missionEvent = new DoMissionEvent(player, resourceEntry, true);
                            Bukkit.getPluginManager().callEvent(missionEvent);

                            if (!missionEvent.isCanceled()) {
                                MissionDao.getInstance().update(resourceEntry);
                            }
                        } catch (JsonProcessingException exception) {
                            exception.printStackTrace();
                        }
                    } else {
                        // Non main, send PMC instead
                        int number;
                        Material itemInHand = player.getItemInHand().getType();
                        if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                            number = getTotalAndSetNull(player, Material.valueOf(resourceMissionJson.getType()));
                        } else {
                            number = player.getItemInHand().getAmount();
                            player.setItemInHand(null);
                        }

                        PluginMessage request = new PluginMessage()
                                .channel("mission:request")
                                .messageUUID(UUID.randomUUID())
                                .dataSize(5)
                                .data(new String[]{"doMission", player.getUniqueId().toString(), MissionType.RESOURCE.toString(), player.getItemInHand().getType().name(), String.valueOf(number)});

                        // Since we gotta have the response from the main server, we need send and wait
                        // And since this is async, we dont need to worry about performance

                        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request);
                        if (response.getData()[0].equalsIgnoreCase("false")) {
                            while (number > 64) {
                                ItemStack itemStack = new ItemStack(itemInHand, 64);
                                player.getInventory().addItem(itemStack);
                                number -= 64;
                            }
                            ItemStack itemStack = new ItemStack(itemInHand, number);
                            player.getInventory().addItem(itemStack);
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onNonMainServerFail"));
                        } else {
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onSuccess")
                                    .replace("%number%", String.valueOf(number)).replace("%type%", resourceMissionJson.getType().toLowerCase(Locale.ROOT)));
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
     * @param player the player
     * @param args   the args
     * @return the boolean
     */
    public boolean sanityCheck(@NotNull Player player, String[] args) {
        MissionDao missionDao = MissionDao.getInstance();
        return new BukkitChecker(instance)
                .target(player)
                .hasPermission("townymission.player")
                .hasTown()
                .hasStarted()
                .isMissionType(MissionType.RESOURCE)
                .customCheck(() -> {
                    // Holding item in hand mismatch
                    MissionEntry resourceEntry = missionDao.getTownStartedMission(TownyUtil.residentOf(player).getUUID(), MissionType.RESOURCE);
                    ResourceMissionJson resourceMissionJson = (ResourceMissionJson) resourceEntry.getMissionJson();
                    if (player.getItemInHand().getType().equals(Material.valueOf(resourceMissionJson.getType()))) {
                        return true;
                    } else {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onNotMatch"));
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.requiredItem").replace("%item%", resourceMissionJson.getType().toLowerCase(Locale.ROOT)));
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.inHandItem").replace("%item%", player.getItemInHand().getType().name().toLowerCase(Locale.ROOT)));
                        return false;
                    }
                })
                .customCheck(() -> {
                    // Mission timeout
                    MissionEntry resourceEntry = missionDao.getTownStartedMission(TownyUtil.residentOf(player).getUUID(), MissionType.RESOURCE);
                    try {
                        if (resourceEntry.isTimedout()) {
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onMissionTimedOut"));
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
                    // Command format error
                    if (args.length == 1)
                        return true;
                    if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                        return true;
                    } else {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                })
                .customCheck(() -> {
                    if (TimerService.getInstance().isInInterval(RankType.SEASON) || TimerService.getInstance().isInInterval(RankType.SPRINT)) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onClickDuringRecess"));
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

    public int getTotalAndSetNull(Player player, Material material) {
        int total = 0;
        int index = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType().equals(material)) {
                total += itemStack.getAmount();
                player.getInventory().setItem(index, null);
            }
            index++;
        }
        return total;
    }
}