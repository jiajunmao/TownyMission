/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.core.components.entity.MissionEntry;
import world.naturecraft.townymission.core.components.enums.RankType;
import world.naturecraft.townymission.core.data.dao.MissionDao;
import world.naturecraft.townymission.core.services.ChatService;
import world.naturecraft.townymission.core.services.MissionService;
import world.naturecraft.townymission.core.services.TimerService;

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
    public TownyMissionStart(TownyMissionBukkit instance) {
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
                        MissionEntry entry = MissionDao.getInstance().getStartedMission(town.getUUID());
                        MissionService.getInstance().startMission(player.getUniqueId(), Integer.parseInt(args[1]));

                        try {
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.start.onSuccess")
                                    .replace("%type%", entry.getMissionType().name())
                                    .replace("%details%", entry.getDisplayLine()));
                        } catch (JsonProcessingException exception) {
                            exception.printStackTrace();
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
    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        // /tm start <num>
        return new BukkitChecker(instance).target(player)
                .hasTown()
                .hasPermission("townymission.player")
                .customCheck(() -> {
                    if (args.length == 1 ||
                            (Integer.parseInt(args[1]) > instance.getConfig().getInt("mission.amount") || Integer.parseInt(args[1]) < 1)) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                }).customCheck(() -> {
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
            for (int i = 1; i <= instance.getConfig().getInt("mission.amount"); i++) {
                tabList.add(String.valueOf(i));
            }
        }
        return tabList;
    }
}
