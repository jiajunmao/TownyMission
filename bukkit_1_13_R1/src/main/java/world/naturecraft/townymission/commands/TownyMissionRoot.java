package world.naturecraft.townymission.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionCommandRoot;

import java.util.List;

/**
 * The type Towny mission root.
 */
public class TownyMissionRoot extends TownyMissionCommandRoot {


    public TownyMissionRoot(TownyMissionBukkit instance) {
        super(instance, 0);
        commandName = "townymission";
        commandAlias = "tms";
    }

    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return false;
    }

    @Override
    protected void postProcessTabList(CommandSender sender, List<String> list) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("townymission.admin")) {
                list.remove("admin");
            }
        }
    }
}
