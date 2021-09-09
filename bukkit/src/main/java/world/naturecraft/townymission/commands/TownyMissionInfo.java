/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
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
import world.naturecraft.townymission.api.exceptions.NoStartedException;
import world.naturecraft.townymission.commands.templates.TownyMissionCommand;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.entity.SprintEntry;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The type Towny mission info.
 */
public class TownyMissionInfo extends TownyMissionCommand implements TabExecutor, CommandExecutor {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionInfo(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean playerSanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .hasTown()
                .hasPermission(new String[]{"townymission.player.info", "townymission.player"})
                .customCheck(() -> {
                    if (args.length == 1) {
                        return true;
                    } else {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandFormatError"));
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
        // /tms info
        // /tms info <town_name>
        if (sender instanceof Player) {
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = (Player) sender;
                    if (sanityCheck(player, args)) {
                        MultilineBuilder builder = new MultilineBuilder("&7------&eTowny Mission: Overview&7------");
                        Town town = TownyUtil.residentOf(player);
                        MissionEntry taskEntry;

                        // Server-wide section
                        builder.add("&5--Basic Info Section--");
                        int currentSeason = instance.getStatsConfig().getInt("season.current");
                        int currentSprint = instance.getStatsConfig().getInt("sprint.current");
                        builder.add("&eCurrent Season: &f" + currentSeason);
                        builder.add("&eCurrent Sprint: &f" + currentSprint);
                        builder.add("&eTown: &f" + town.getName());

                        // Started Mission section
                        builder.add("&5--Mission Section--");
                        if (MissionService.getInstance().hasStarted(town.getUUID())) {
                            taskEntry = MissionDao.getInstance().getStartedMissions(town.getUUID()).get(0);
                            builder.add("&eCurrent Mission: &f" + taskEntry.getMissionJson().getDisplayLine());
                            Player startedPlayer = Bukkit.getPlayer(taskEntry.getStartedPlayerUUID());
                            builder.add("&eStarted By: &f" + startedPlayer.getName());

                            long startedTime = taskEntry.getStartedTime();
                            long allowedTime = taskEntry.getAllowedTime();

                            DateFormat timeFormat = new SimpleDateFormat("dd/MM HH:mm");
                            Date tempDate = new Date(startedTime);
                            builder.add("&eStarted Time: &f" + timeFormat.format(tempDate));

                            String display = String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(allowedTime),
                                    TimeUnit.MILLISECONDS.toMinutes(allowedTime) -
                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(allowedTime)));
                            builder.add("&eAllowed Time: &f" + display);

                            try {
                                if (taskEntry.isTimedout()) {
                                    builder.add("&Remaining Time: &cTimed Out");
                                } else {
                                    Date dateNow = new Date();
                                    long remainingTime = (startedTime + allowedTime) - dateNow.getTime();
                                    display = String.format("%02d:%02d",
                                            TimeUnit.MILLISECONDS.toHours(remainingTime),
                                            TimeUnit.MILLISECONDS.toMinutes(remainingTime) -
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)));
                                    builder.add("&eRemaining Time: &f" + display);
                                }
                            } catch (NoStartedException e) {
                                // Ignore, not possible
                            }
                        } else {
                            builder.add("&eCurrent Mission: &cNone");
                        }

                        // Participant info section
                        builder.add("&5--Participant Section--");
                        int baseline = instance.getConfig().getInt("participants.sprintRewardBaseline");
                        int memberScale = instance.getConfig().getInt("participants.sprintRewardMemberScale");
                        int baselineCap = instance.getConfig().getInt("participants.sprintRewardBaselineCap");
                        int baselineIncrement = instance.getConfig().getInt("participants.sprintBaselineIncrement");

                        int realBaseline = baseline + (town.getNumResidents() - 1) * memberScale + (currentSprint - 1) * baselineIncrement;
                        realBaseline = realBaseline > baselineCap ? baseline : realBaseline;

                        SprintEntry sprintEntry = SprintDao.getInstance().get(town.getUUID());
                        int naturepoints = sprintEntry == null ? 0 : sprintEntry.getNaturepoints();

                        builder.add("&eTotal Points: &f" + naturepoints);
                        builder.add("&eBaseline: &f" + realBaseline);

                        int rankingPoints = (naturepoints - realBaseline) / town.getNumResidents();
                        rankingPoints = Math.max(rankingPoints, 0);
                        builder.add("&eRanking Points: &f" + rankingPoints);

                        String finalString = builder.toString();
                        ChatService.getInstance().sendMsg(player.getUniqueId(), finalString);
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
