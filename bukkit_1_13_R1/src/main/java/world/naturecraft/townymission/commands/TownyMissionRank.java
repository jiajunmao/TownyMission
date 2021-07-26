/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.data.dao.SeasonDao;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.RankingService;
import world.naturecraft.townymission.services.TimerService;
import world.naturecraft.townymission.utils.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * The type Towny mission rank.
 */
public class TownyMissionRank extends TownyMissionCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionRank(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .hasPermission("townymission.player")
                .customCheck(() -> {
                    if (args.length == 2 && (args[1].equalsIgnoreCase("sprint") || args[1].equalsIgnoreCase("season"))) {
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
        // /tms rank sprint
        // /tms rank season
        if (sender instanceof Player) {

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = (Player) sender;
                    if (!sanityCheck(player, args)) return;

                    RankType rankType = RankType.valueOf(args[1].toUpperCase(Locale.ROOT));
                    List<Rankable> entryList = RankingService.getInstance().getRanks(rankType);

                    MultilineBuilder builder = new MultilineBuilder("&7------&eTowny Mission: " + Util.capitalizeFirst(args[1]) + " Rank&7------");

                    int index = 1;
                    for (Rankable entry : entryList) {
                        Town town = TownyUtil.getTown(UUID.fromString(entry.getRankingId()));

                        switch (RankType.valueOf(args[1].toUpperCase(Locale.ROOT))) {
                            case SPRINT:
                                builder.add("&e" + index + ". &3" + town.getName() + " &f: "
                                        + Rankable.getRankingPoints(town.getNumResidents(), entry.getRankingFactor(), instance)
                                        + " points");
                                break;
                            case SEASON:
                                builder.add("&e" + index + ". &3" + town.getName() + " &f: " + entry.getRankingFactor() + " points");
                                break;
                        }

                        index++;
                    }
                    String finalString = builder.toString();
                    ChatService.getInstance().sendMsg(player.getUniqueId(), finalString);

                }
            };

            r.runTaskAsynchronously(instance);
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
        List<String> tabList = new ArrayList<>();

        tabList.add("sprint");
        tabList.add("season");
        return tabList;
    }
}
