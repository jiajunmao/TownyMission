package world.naturecraft.townymission.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import world.naturecraft.townymission.TownyMission;

import java.util.logging.Logger;

/**
 * The type Towny mission command.
 */
public abstract class TownyMissionCommand implements TabExecutor, CommandExecutor {

    /**
     * The Instance.
     */
    protected TownyMission instance;
    /**
     * The Logger.
     */
    protected Logger logger = instance.getLogger();

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionCommand(TownyMission instance) {
        this.instance = instance;
    }
}
