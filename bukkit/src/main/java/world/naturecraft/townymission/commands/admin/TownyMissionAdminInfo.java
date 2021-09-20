package world.naturecraft.townymission.commands.admin;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.naturelib.utils.MultilineBuilder;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.api.exceptions.NoStartedException;
import world.naturecraft.townymission.commands.templates.TownyMissionAdminCommand;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.entity.SprintEntry;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.core.RankingService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TownyMissionAdminInfo extends TownyMissionAdminCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminInfo(TownyMissionBukkit instance) {
        super(instance);
    }

    /**
     * Sanity check boolean.
     *
     * @param player the player
     * @param args   the args
     * @return the boolean
     */
    public boolean playerSanityCheck(@NotNull Player player, @NotNull String[] args) {
        // /tmsa info <town>
        BukkitChecker checker = new BukkitChecker(instance).target(player).silent(false)
                .hasPermission(new String[]{"townymission.admin.info", "townymission.admin"});

        return checker.check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        BukkitChecker checker = new BukkitChecker(instance)
                .customCheck(() -> {
                    if (args.length != 2 || !args[0].equalsIgnoreCase("info")) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    String townName = args[1];
                    if (TownyUtil.getTown(townName) == null) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onTownNameInvalid"));
                        return false;
                    }
                    return true;
                });

        return checker.check();
    }

    /**
     * Executes the given command, returning its success
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (!sanityCheck(sender, args)) return;

                MultilineBuilder builder = new MultilineBuilder(instance.getGuiLangEntry("mission_info.sections.title"));
                Town town = TownyUtil.getTown(args[1]);
                MissionEntry taskEntry;

                // Server-wide section
                builder.add(instance.getGuiLangEntry("mission_info.sections.basic_info.title"));
                int currentSeason = instance.getStatsConfig().getInt("season.current");
                int currentSprint = instance.getStatsConfig().getInt("sprint.current");
                for (String s : instance.getGuiLangEntries("mission_info.sections.basic_info.lores")) {
                    s = s.replace("%season%", String.valueOf(currentSeason))
                            .replace("%sprint", String.valueOf(currentSprint))
                            .replace("%town_name%", town.getName());
                    builder.add(s);
                }

                // Started Mission section
                builder.add(instance.getGuiLangEntry("mission_info.sections.mission.title"));
                if (!MissionDao.getInstance().getStartedMissions(town.getUUID()).isEmpty()) {
                    taskEntry = MissionDao.getInstance().getStartedMissions(town.getUUID()).get(0);
                    Player startedPlayer = Bukkit.getPlayer(taskEntry.getStartedPlayerUUID());

                    long allowedTime = taskEntry.getAllowedTime();

                    DateFormat timeFormat = new SimpleDateFormat("dd/MM HH:mm");

                    String allowedTimeDisplay = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(allowedTime),
                            TimeUnit.MILLISECONDS.toMinutes(allowedTime) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(allowedTime)));

                    long remainingTime = (taskEntry.getStartedTime() + allowedTime) - new Date().getTime();
                    String remainingTimeDisplay = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(remainingTime),
                            TimeUnit.MILLISECONDS.toMinutes(remainingTime) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)));

                    for (String s : instance.getGuiLangEntries("mission_info.sections.mission.lores")) {
                        s = s.replace("%display_line%", taskEntry.getMissionJson().getDisplayLine())
                                .replace("%player_name%", startedPlayer.getName())
                                .replace("%started_time%", timeFormat.format(new Date(taskEntry.getStartedTime())))
                                .replace("%allowed_time%", allowedTimeDisplay)
                                .replace("%remaining_time%", remainingTimeDisplay);
                        builder.add(s);
                    }
                } else {
                    builder.add(instance.getGuiLangEntries("mission_info.sections.mission.lores").get(0).replace("%display_line%", "&cNone"));
                }

                // Participant info section
                builder.add(instance.getGuiLangEntry("mission_info.sections.participant.title"));

                SprintEntry sprintEntry = SprintDao.getInstance().get(town.getUUID());
                int naturepoints = sprintEntry == null ? 0 : sprintEntry.getNaturepoints();

                for(String s : instance.getGuiLangEntries("mission_info.sections.participant.lores")) {
                    s = s.replace("%total_points%", String.valueOf(naturepoints))
                            .replace("%baseline_points%", String.valueOf(RankingService.getInstance().getTownBaseline(town.getUUID())))
                            .replace("%ranking_points%", String.valueOf(RankingService.getInstance().getRankingPoints(town.getUUID())));

                    builder.add(s);
                }


                String finalString = builder.toString();
                sender.sendMessage(finalString);
            }
        };

        r.runTaskAsynchronously(instance);

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
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // /tmsa info <town>
        List<String> tabList = new ArrayList<>();
        if (args.length == 2) {
            for (Town town : TownyUtil.getTowns()) {
                tabList.add(town.getName());
            }
        }

        return tabList;
    }
}
