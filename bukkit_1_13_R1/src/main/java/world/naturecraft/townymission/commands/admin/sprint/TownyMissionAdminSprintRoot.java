package world.naturecraft.townymission.commands.admin.sprint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.TownyMissionCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TownyMissionAdminSprintRoot extends TownyMissionCommand {

    /**
     * The Commands.
     */
    Map<String, TownyMissionCommand> commands;

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminSprintRoot(TownyMissionBukkit instance) {
        super(instance);
        commands = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // /tmsa season something
            if (commands.containsKey(args[1])) {
                getExecutor(args[1]).onCommand(sender, command, alias, args);
            } else {
                onUnknown(player);
            }
        }


        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>(commands.keySet());
        List<String> tabList;

        // /tmsa sprint
        if (args.length == 2) {
            tabList = arguments;
        } else if (commands.containsKey(args[1])) {
            tabList = commands.get(args[1]).onTabComplete(sender, command, alias, args);
        } else {
            tabList = null;
        }
        return tabList;
    }

    /**
     * Register admin command.
     *
     * @param name         the name
     * @param adminCommand the admin command
     */
    public void registerAdminCommand(String name, TownyMissionCommand adminCommand) {
        commands.put(name, adminCommand);
    }

    /**
     * Gets executor.
     *
     * @param name the name
     * @return the executor
     */
    public TownyMissionCommand getExecutor(String name) {
        return commands.getOrDefault(name, null);
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
        return false;
    }
}
