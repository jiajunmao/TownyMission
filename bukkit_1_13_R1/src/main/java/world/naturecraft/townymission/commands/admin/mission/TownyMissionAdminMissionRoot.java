package world.naturecraft.townymission.commands.admin.mission;

import org.bukkit.command.CommandSender;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionCommandRoot;

import java.util.List;

public class TownyMissionAdminMissionRoot extends TownyMissionCommandRoot {

    public TownyMissionAdminMissionRoot(TownyMissionBukkit instance) {
        super(instance, 1);
    }

    @Override
    protected void postProcessTabList(CommandSender commandSender, List<String> list) {
        // Do nothing
    }
}
