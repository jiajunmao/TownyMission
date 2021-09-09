package world.naturecraft.townymission.commands.admin.season;

import org.bukkit.command.CommandSender;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionCommandRoot;

import java.util.List;

public class TownyMissionAdminSeasonRoot extends TownyMissionCommandRoot {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminSeasonRoot(TownyMissionBukkit instance) {
        super(instance, 1, "townymissionadmin", "tmsa");
    }

    @Override
    protected void postProcessTabList(CommandSender commandSender, List<String> list) {
        // Do nothing
    }
}
