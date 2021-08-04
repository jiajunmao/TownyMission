package world.naturecraft.townymission.commands.admin.mission;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.TownyMissionCommand;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.List;

public class TownyMissionAdminMissionAbort extends TownyMissionCommand {
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
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        // /tmsa mission abort <town>
        String townName = args[2];
        BukkitChecker checker = new BukkitChecker(instance).target(player).silent(false)
                .hasPermission("townymission.admin")
                .customCheck(() -> {
                    if (args.length != 3 || !args[0].equalsIgnoreCase("mission") || !args[1].equalsIgnoreCase("abort")) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    if (TownyUtil.getTown(townName) == null) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onTownNameInvalid"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    // Check whether the town has a started mission ,cannot use hasStarted() because it is not a player target
                    Town town = TownyUtil.getTown(townName);
                    if (MissionService.getInstance().hasStarted(town.getUUID())) {
                        return true;
                    } else {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("adminCommands.mission_abort.onNoStartedMission")
                                .replace("%town%", townName));
                        return false;
                    }
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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!sanityCheck(player, args)) return;

                    Town town = TownyUtil.getTown(args[2]);
                    MissionEntry missionEntry = MissionDao.getInstance().getStartedMission(town.getUUID());

                    MissionService.getInstance().abortMission(null, missionEntry, true);
                    ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("adminCommands.mission_abort.onSuccess")
                            .replace("%town%", town.getName()));
                }
            };

            r.runTaskAsynchronously((TownyMissionBukkit) instance);
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
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
