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
import world.naturecraft.townymission.components.containers.sql.SprintEntry;
import world.naturecraft.townymission.components.containers.sql.MissionHistoryEntry;
import world.naturecraft.townymission.utils.MultilineBuilder;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
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
                            .customCheck(() -> {
                                        if (args.length == 1
                                                || (args.length == 2
                                                    && Util.isInt(args[1])
                                                    && Integer.parseInt(args[1]) >= 1
                                                    && Integer.parseInt(args[1]) <= instance.getConfig().getInt("mission.amount"))
                                                || (args.length == 2 && args[1].equalsIgnoreCase("all"))) {
                                            return true;
                                        } else {
                                            Util.sendMsg(player, instance.getLangEntry("universal.onCommandFormatError"));
                                            return false;
                                        }
                                    }
                            ).check();

                    if (sane) {
                        Town town = TownyUtil.residentOf(player);
                        List<MissionHistoryEntry> list = missionHistoryDao.getAllUnclaimed(town);

                        if (list.size() == 0) {
                            Util.sendMsg(player, instance.getLangEntry("commands.claim.onNotFound"));
                            return;
                        } else {
                            //TODO: Check matching season and sprint

                            // Listing all the claims
                            if (args.length == 1) {
                                MultilineBuilder builder = new MultilineBuilder("&7------&eTowny Mission: Unclaimed Missions&7------");
                                int index = 1;
                                for (MissionHistoryEntry e : list) {
                                    builder.add("&e" + index + ". Type&f: " + e.getMissionType() + " " + e.getMissionJson().getDisplayLine());
                                    index++;
                                }
                                Util.sendMsg(player, builder.toString());
                            } else if (args.length == 2 && Util.isInt(args[1])) {
                                int choice = Integer.parseInt(args[1]) - 1;
                                if (choice >= list.size()) {
                                    Util.sendMsg(player, instance.getLangEntry("commands.claim.notValidIndex"));
                                    return;
                                }

                                MissionHistoryEntry entry = list.get(choice);
                                int reward = entry.getMissionJson().getReward();

                                // This means that sprint database does not contain the town info yet, adding
                                SprintEntry sprintEntry;
                                if ((sprintEntry = sprintDao.get(town.getUUID().toString())) == null) {
                                    SprintEntry newSprintEntry = new SprintEntry(0, town.getUUID().toString(), town.getName(), 0, 0, 0);
                                    sprintDao.add(newSprintEntry);
                                    sprintEntry = sprintDao.get(town.getUUID().toString());
                                }

                                sprintEntry.setNaturepoints(sprintEntry.getNaturepoints() + reward);
                                sprintDao.update(sprintEntry);


                                entry.setClaimed(true);
                                missionHistoryDao.update(entry);
                                Util.sendMsg(player, instance.getLangEntry("commands.claim.onSuccess").replace("%points%", String.valueOf(reward)));
                            } else {
                                // Claim all rewards
                                //TODO: command format sanity check in sanity checker

                                if (sprintDao.get(town.getUUID().toString()) == null) {
                                    SprintEntry newSprintEntry = new SprintEntry(0, town.getUUID().toString(), town.getName(), 0, 0, 0);
                                    sprintDao.add(newSprintEntry);
                                }

                                int totalPoints = 0;
                                SprintEntry sprintEntry = sprintDao.get(town.getUUID().toString());
                                System.out.println("Sprint entry id: " + sprintEntry.getId());

                                for (MissionHistoryEntry taskHistoryEntry : list) {
                                    totalPoints += taskHistoryEntry.getMissionJson().getReward();
                                    sprintEntry.setNaturepoints(sprintEntry.getNaturepoints() + taskHistoryEntry.getMissionJson().getReward());
                                    taskHistoryEntry.setClaimed(true);
                                    missionHistoryDao.update(taskHistoryEntry);
                                }

                                sprintDao.update(sprintEntry);
                                Util.sendMsg(player, instance.getLangEntry("commands.claim.onSuccess").replace("%points%", String.valueOf(totalPoints)));
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
        List<String> tabList = new ArrayList<>();
        if (args.length == 2) {
            tabList.add("all");
            tabList.add("#num");
        }
        return tabList;
    }
}
