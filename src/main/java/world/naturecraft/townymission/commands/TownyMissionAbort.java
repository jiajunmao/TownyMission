package world.naturecraft.townymission.commands;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.List;

public class TownyMissionAbort extends TownyMissionCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAbort(TownyMission instance) {
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
            Player player = (Player) sender;
            if (sanityCheck(player)) {
                Town town = TownyUtil.residentOf(player);
                List<TaskEntry> taskEntries = taskDao.getTownTasks(town);
                for (TaskEntry e : taskEntries) {
                    if (e.getStartedTime() != 0) {
                        taskDao.remove(e);
                        Util.sendMsg(sender, "&c The mission has been aborted");
                    }
                }
            }
        }

        return true;
    }

    public boolean sanityCheck(Player player) {
        if (player.hasPermission("townymission.player")) {
            Town town;
            if ((town = TownyUtil.residentOf(player)) != null) {
                TaskEntry entry;
                if ((entry = taskDao.getStartedMission(town)) != null) {
                    if (entry.getStartedPlayer().equalsIgnoreCase(player.getUniqueId().toString()) || TownyUtil.mayorOf(player) != null) {
                        return true;
                    } else {
                        Util.sendMsg(player, "&c You either need to be the mayor or started the mission yourself to abort!");
                        return false;
                    }
                } else {
                    Util.sendMsg(player, "&c Your town does not have a started missions!");
                    return false;
                }
            } else {
                Util.sendMsg(player, "&c You need to belong to a town to start a mission!");
                return false;
            }
        } else {
            Util.sendMsg(player, "&c You do not have permission for this command!");
            return false;
        }
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
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}