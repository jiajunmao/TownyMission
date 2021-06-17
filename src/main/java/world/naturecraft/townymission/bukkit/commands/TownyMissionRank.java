/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.commands;

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
import world.naturecraft.townymission.bukkit.utils.RankUtil;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.core.components.entity.Rankable;
import world.naturecraft.townymission.core.components.enums.RankType;
import world.naturecraft.townymission.core.data.dao.SprintDao;
import world.naturecraft.townymission.core.services.TimerService;
import world.naturecraft.townymission.core.utils.MultilineBuilder;

import java.util.ArrayList;
import java.util.List;

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
                .customCheck(() -> {
                    if (args.length == 2 && (args[1].equalsIgnoreCase("sprint") || args[1].equalsIgnoreCase("season"))) {
                        return true;
                    } else {
                        BukkitUtil.sendMsg(player, instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                })
                .customCheck(() -> {
                    if (TimerService.getInstance().isInInterval(RankType.SEASON) || TimerService.getInstance().isInInterval(RankType.SPRINT)) {
                        BukkitUtil.sendMsg(player, instance.getLangEntry("universal.onClickDuringRecess"));
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
                    if (sanityCheck(player, args)) {
                        List<Rankable> entryList;
                        if (args[1].equalsIgnoreCase("sprint"))
                            entryList = (List<Rankable>) RankUtil.sort(SprintDao.getInstance().getEntries());
                        else
                            entryList = (List<Rankable>) RankUtil.sort(SprintDao.getInstance().getEntries());
                        MultilineBuilder builder = new MultilineBuilder("&7------&eTowny Mission: Sprint Rank&7------");
                        int index = 1;
                        for (Rankable entry : entryList) {
                            Town town = TownyUtil.residentOf(player);
                            if (args[1].equalsIgnoreCase("sprint")) {
                                builder.add("&e" + index + ". &f" + town.getName() + " : "
                                        + BukkitUtil.getRankingPoints(town.getNumResidents(), entry.getRankingFactor(), instance)
                                        + " points");
                            } else {
                                builder.add("&e" + index + ". &f" + town.getName() + " : " + entry.getRankingFactor() + " points");
                            }
                        }
                        String finalString = builder.toString();
                        BukkitUtil.sendMsg(player, finalString);
                    }
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
