package world.naturecraft.townymission.commands.admin.season;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionCommandRoot;

import java.util.ArrayList;
import java.util.List;

public class TownyMissionAdminSeasonRoot extends TownyMissionCommandRoot {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminSeasonRoot(TownyMissionBukkit instance) {
        super(instance, 1);
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


    @Override
    protected void postProcessTabList(CommandSender commandSender, List<String> list) {
        // Do nothing
    }
}
