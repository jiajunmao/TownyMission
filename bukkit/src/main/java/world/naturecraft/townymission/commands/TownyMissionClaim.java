/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.naturelib.utils.MultilineBuilder;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.api.exceptions.NotEnoughInvSlotException;
import world.naturecraft.townymission.commands.templates.TownyMissionCommand;
import world.naturecraft.townymission.components.entity.ClaimEntry;
import world.naturecraft.townymission.data.dao.ClaimDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.core.RewardService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Towny mission claim.
 */
public class TownyMissionClaim extends TownyMissionCommand implements TabExecutor, CommandExecutor {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionClaim(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean playerSanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .hasTown()
                .hasPermission(new String[]{"townymission.player.claim", "townymission.player"})
                .customCheck(() -> {
                    if (args.length == 1
                            || (args.length == 2
                            && Util.isInt(args[1])
                            && Integer.parseInt(args[1]) >= 1
                            && Integer.parseInt(args[1]) <= instance.getConfig().getInt("mission.amount"))
                            || (args.length == 2 && args[1].equalsIgnoreCase("all"))) {
                        return true;
                    } else {
                        player.sendMessage(instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                }).check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) return true;

        commandSender.sendMessage(instance.getLangEntry("universal.onPlayerCommandInConsole"));
        return false;
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

                    List<ClaimEntry> claimEntries = ClaimDao.getInstance().getEntries(data -> (data.getPlayerUUID().equals(player.getUniqueId())
                            && data.getSeason() == instance.getStatsConfig().getInt("season.current")
                            && data.getSprint() == instance.getStatsConfig().getInt("sprint.current")));

                    if (claimEntries.size() == 0) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.claim.onNotFound"));
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
                            ChatService.getInstance().sendMsg(player.getUniqueId(), builder.toString());
                        } else if (args.length == 2 && Util.isInt(args[1])) {
                            // This is claiming one claim entry from all
                            int choice = Integer.parseInt(args[1]) - 1;
                            if (choice >= claimEntries.size()) {
                                ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.claim.notValidIndex"));
                                return;
                            }

                            ClaimEntry entry = claimEntries.get(choice);
                            try {
                                RewardService.getInstance().claimEntry(player.getUniqueId(), entry);
                            } catch (NotEnoughInvSlotException e) {
                                ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.claim.onNotEnoughSlot"));
                                return;
                            }
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.claim.onSuccess"));
                        } else {
                            // Claim all rewards
                            RewardService.getInstance().claimEntry(player.getUniqueId(), claimEntries);
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.claim.onSuccess"));
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
