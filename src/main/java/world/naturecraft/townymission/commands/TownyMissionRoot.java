package world.naturecraft.townymission.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Towny mission root.
 */
public class TownyMissionRoot extends TownyMissionCommand {

    private final Map<String, TownyMissionCommand> commands;

    /**
     * Instantiates a new Towny mission root.
     *
     * @param instance the instance
     */
    public TownyMissionRoot(TownyMission instance) {
        super(instance);
        commands = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (commands.containsKey(args[0])) {
            getExecutor(args[0]).onCommand(sender, command, alias, args);
        } else {
            onUnknown(sender);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("townymission") || alias.equalsIgnoreCase("tm")) {
            List<String> arguments = new ArrayList<>(commands.keySet());
            List<String> tabList;

            if (args.length == 1) {
                // This is only /townymission
                tabList = arguments;
            } else if (args.length == 2 && commands.containsKey(args[0])) {
                tabList = commands.get(args[0]).onTabComplete(sender, command, alias, args);
            } else {
                tabList = null;
            }
            return tabList;
        } else {
            return null;
        }
    }

    /**
     * Register command.
     *
     * @param name     the name
     * @param executor the executor
     */
    public void registerCommand(String name, TownyMissionCommand executor) {
        commands.put(name, executor);
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
}
