/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.MissionJson;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.config.CustomConfigParser;
import world.naturecraft.townymission.dao.TaskDao;
import world.naturecraft.townymission.db.sql.TaskDatabase;
import world.naturecraft.townymission.utils.MultilineBuilder;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class TownyMissionList extends TownyMissionCommand {


    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionList(TownyMission instance) {
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
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = (Player) sender;
                    Town town;
                    if ((town = TownyUtil.residentOf(player)) != null) {
                        int diff = 15 - taskDao.getNumAdded(town);
                        List<MissionJson> missions = CustomConfigParser.parseAll(instance);
                        int size = missions.size();
                        Random rand = new Random();

                        for (int i = 0; i < diff; i++) {
                            //TODO: Prevent duplicates
                            int index = rand.nextInt(size);
                            MissionJson mission = missions.get(index);
                            try {
                                TaskEntry entry = new TaskEntry(0,
                                        mission.getMissionType().name(),
                                        Util.currentTime(),
                                        0,
                                        Util.hrToMs(mission.getHrAllowed()),
                                        mission.toJson(),
                                        town.getName());
                                taskDao.add(entry);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }

                        MultilineBuilder builder = new MultilineBuilder("&7------&eTowny Mission: Current Assignments&7------");
                        int index = 1;

                        for (TaskEntry e : taskDao.getTownTask(town)) {
                            try {
                                builder.add("&e" + index + ". Type&f: " + e.getTaskType() + " " + e.getDisplayLine());
                            } catch (JsonProcessingException exp) {
                                logger.severe("Json parsing error when parsing " + e.getTaskJson());
                                exp.printStackTrace();
                            }
                            index++;
                        }

                        Util.sendMsg(sender, Util.translateColor(builder.toString()));

                    } else {
                        Util.sendMsg(sender, "&c You are not a member of a town. You need to be in a town to work on Towny Mission");
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
