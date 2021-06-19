package world.naturecraft.townymission.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.services.ChatService;

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
    public TownyMissionRoot(TownyMissionBukkit instance) {
        super(instance);
        commands = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                ChatService.getInstance().sendMsg(player.getUniqueId(), "&f I am too lazy to make a help page again -Barb");
                return false;
            }

            if (commands.containsKey(args[0])) {
                getExecutor(args[0]).onCommand(sender, command, alias, args);
            } else {
                onUnknown(player);
            }
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
                // This is only /tms
                tabList = arguments;
            } else if (commands.containsKey(args[0])) {
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

    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return false;
    }
}
