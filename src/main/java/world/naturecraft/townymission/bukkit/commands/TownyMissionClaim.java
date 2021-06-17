/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.exceptions.NotEnoughInvSlotException;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.components.entity.ClaimEntry;
import world.naturecraft.townymission.core.data.dao.ClaimDao;
import world.naturecraft.townymission.core.services.RewardService;
import world.naturecraft.townymission.core.utils.EntryFilter;
import world.naturecraft.townymission.core.utils.MultilineBuilder;
import world.naturecraft.townymission.core.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Towny mission claim.
 */
public class TownyMissionClaim extends TownyMissionCommand {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionClaim(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .hasTown()
                .customCheck(() -> {
                            if (args.length == 1
                                    || (args.length == 2
                                    && Util.isInt(args[1])
                                    && Integer.parseInt(args[1]) >= 1
                                    && Integer.parseInt(args[1]) <= instance.getConfig().getInt("mission.amount"))
                                    || (args.length == 2 && args[1].equalsIgnoreCase("all"))) {
                                return true;
                            } else {
                                BukkitUtil.sendMsg(player, instance.getLangEntry("universal.onCommandFormatError"));
                                return false;
                            }
                        }
                ).check();

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
        if (sender instanceof Player) {
            // /tms claim
            // /tms claim all
            // /tms claim <num>

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = (Player) sender;
                    if (!sanityCheck(player, args)) return;

                    List<ClaimEntry> claimEntries = ClaimDao.getInstance().getEntries(new EntryFilter<ClaimEntry>() {
                        @Override
                        public boolean include(ClaimEntry data) {
                            return (data.getPlayerUUID().equals(player.getUniqueId())
                                    && data.getSeason() == instance.getStatsConfig().getInt("season.current")
                                    && data.getSprint() == instance.getStatsConfig().getInt("sprint.current"));
                        }
                    });

                    if (claimEntries.size() == 0) {
                        BukkitUtil.sendMsg(player, instance.getLangEntry("commands.claim.onNotFound"));
                        return;
                    } else {
                        // Listing all the claims
                        if (args.length == 1) {
                            MultilineBuilder builder = new MultilineBuilder("&7------&eTowny Mission: Unclaimed Missions&7------");
                            int index = 1;
                            for (ClaimEntry e : claimEntries) {
                                builder.add("&e" + index + ". Type&f: " + e.getRewardJson().getRewardType() + " " + e.getRewardJson().getDisplayLine());
                                index++;
                            }
                            BukkitUtil.sendMsg(player, builder.toString());
                        } else if (args.length == 2 && Util.isInt(args[1])) {
                            // This is claiming one claim entry from all
                            int choice = Integer.parseInt(args[1]) - 1;
                            if (choice >= claimEntries.size()) {
                                BukkitUtil.sendMsg(player, instance.getLangEntry("commands.claim.notValidIndex"));
                                return;
                            }

                            ClaimEntry entry = claimEntries.get(choice);
                            try {
                                RewardService.getInstance().claimEntry(player, entry);
                            } catch (NotEnoughInvSlotException e) {
                                BukkitUtil.sendMsg(player, instance.getLangEntry("commands.claim.onNotEnoughSlot"));
                                return;
                            }
                            BukkitUtil.sendMsg(player, instance.getLangEntry("commands.claim.onSuccess"));
                        } else {
                            // Claim all rewards
                            RewardService.getInstance().claimEntry(player, claimEntries);
                            BukkitUtil.sendMsg(player, instance.getLangEntry("commands.claim.onSuccess"));
                        }
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
        if (args.length == 2) {
            tabList.add("all");
            tabList.add("#num");
        }
        return tabList;
    }
}
