package world.naturecraft.townymission.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import world.naturecraft.townymission.TownyMission;

import java.util.logging.Logger;

public abstract class TownyMissionCommand implements TabExecutor, CommandExecutor {

    protected TownyMission instance;
    protected Logger logger = instance.getLogger();

    public TownyMissionCommand(TownyMission instance) {
        this.instance = instance;
    }
}
