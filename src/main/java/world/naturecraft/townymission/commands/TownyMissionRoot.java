package world.naturecraft.townymission.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.utils.Util;

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
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commands.containsKey(strings[0])) {
            getExecutor(strings[0]).onCommand(commandSender, command, s, strings);
        } else {
            onUnknown(commandSender);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
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

    public void onUnknown(CommandSender sender) {
        Util.sendMsg(sender, "&c The command you are looking for does not exist");
    }
}
