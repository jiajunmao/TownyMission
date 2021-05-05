package world.naturecraft.townymission.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TownyMissionRoot extends TownyMissionCommand {

    private Map<String, TownyMissionCommand> commands;

    public TownyMissionRoot(TownyMission instance) {
        super(instance);
        commands = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }

    public void registerCommand(String name, TownyMissionCommand executor) {
        commands.put(name, executor);
    }

    public TownyMissionCommand getExecutor(String name) {
        return commands.getOrDefault(name, null);
    }
}
