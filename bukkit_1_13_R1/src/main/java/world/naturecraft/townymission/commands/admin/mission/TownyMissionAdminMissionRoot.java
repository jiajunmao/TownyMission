package world.naturecraft.townymission.commands.admin.mission;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionCommandRoot;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.ArrayList;
import java.util.List;

public class TownyMissionAdminMissionRoot extends TownyMissionCommandRoot {

    public TownyMissionAdminMissionRoot(TownyMissionBukkit instance) {
        super(instance, 1, "townymissionadmin", "tmsa");
    }

    @Override
    protected void postProcessTabList(CommandSender commandSender, List<String> list) {

    }
}
