package world.naturecraft.townymission.commands.admin.mission;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionAdminCommand;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.services.core.TimerService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.ArrayList;
import java.util.List;

public class TownyMissionAdminMissionAbort extends TownyMissionAdminCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminMissionAbort(TownyMissionBukkit instance) {
        super(instance);
    }

    /**
     * Sanity check boolean.
     *
     * @param player the player
     * @param args   the args
     * @return the boolean
     */
    @Override
    public boolean playerSanityCheck(@NotNull Player player, @NotNull String[] args) {
        // /tmsa mission abort <town>
        BukkitChecker checker = new BukkitChecker(instance).target(player).silent(false)
                .hasPermission(new String[]{"townymission.admin.mission.abort", "townymission.admin"});

        return checker.check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        return new BukkitChecker(instance)
                .customCheck(() -> {
                    if (args.length != 3 || !args[0].equalsIgnoreCase("mission") || !args[1].equalsIgnoreCase("abort")) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    // Recess check
                    if (TimerService.getInstance().isInInterval(RankType.SEASON) || TimerService.getInstance().isInInterval(RankType.SPRINT)) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onClickDuringRecess"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    String townName = args[2];
                    if (TownyUtil.getTown(townName) == null) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onTownNameInvalid"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    String townName = args[2];
                    // Check whether the town has a started mission
                    Town town = TownyUtil.getTown(townName);
                    if (MissionService.getInstance().hasStarted(town.getUUID())) {
                        return true;
                    } else {
                        commandSender.sendMessage(instance.getLangEntry("adminCommands.mission_abort.onNoStartedMission")
                                .replace("%town%", townName));
                        return false;
                    }
                }).check();
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
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (!sanityCheck(sender, args)) return;

                Town town = TownyUtil.getTown(args[2]);
                MissionEntry missionEntry = MissionDao.getInstance().getStartedMissions(town.getUUID()).get(0);

                MissionService.getInstance().abortMission(null, missionEntry, true);
                sender.sendMessage(instance.getLangEntry("adminCommands.mission_abort.onSuccess")
                        .replace("%town%", town.getName()));
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
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabList = new ArrayList<>();
        // /tmsa mission abort <town>
        if (args.length == 3) {
            for (Town town : TownyUtil.getTowns()) {
                tabList.add(town.getName());
            }
        }

        return tabList;
    }
}
