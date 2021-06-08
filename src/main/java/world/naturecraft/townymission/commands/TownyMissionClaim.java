/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.TaskHistoryEntry;
import world.naturecraft.townymission.utils.MultilineBuilder;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.List;

public class TownyMissionClaim extends TownyMissionCommand {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionClaim(TownyMission instance) {
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
        if (sender instanceof Player) {
            // /tms claim
            // /tms claim all
            // /tms claim <num>

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                Player player = (Player) sender;

                boolean sane = new SanityChecker(instance).target(player)
                        .hasTown()
                        .customCheck(() -> args.length == 1 || args.length == 2).check();

                if (sane) {
                    Town town = TownyUtil.residentOf(player);
                    List<TaskHistoryEntry> list = taskHistoryDao.getAllUnclaimed(town);

                    if (list.size() == 0) {
                        Util.sendMsg(player, Util.getLangEntry("commands.claim.onNotFound", instance));
                        return;
                    } else {
                        //TODO: Check matching season and sprint

                        // Listing all the claims
                        if (args.length == 1) {
                            MultilineBuilder builder = new MultilineBuilder("&7------&eTowny Mission: Unclaimed Missions&7------");
                            int index= 1;
                            for (TaskHistoryEntry e : list) {
                                builder.add("&e" + index + ". Type&f: " + e.getMissionType() + " " + e.getMissionJson().getDisplayLine());
                                index++;
                            }
                            Util.sendMsg(player, builder.toString());
                        } else if (args.length == 2 && Util.isInt(args[1])) {
                            int choice = Integer.parseInt(args[1]) - 1;
                            if (choice >= list.size()) {
                                Util.sendMsg(player, Util.getLangEntry("commands.claim.notValidIndex", instance));
                                return;
                            }

                            TaskHistoryEntry entry = list.get(choice);
                        }
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
        return null;
    }
}
