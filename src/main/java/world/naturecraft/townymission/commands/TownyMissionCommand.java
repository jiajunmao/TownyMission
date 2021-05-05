package world.naturecraft.townymission.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.utils.Util;

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
    protected Logger logger;

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionCommand(TownyMission instance) {
        this.instance = instance;
        this.logger = instance.getLogger();
    }

    public void onUnknown(CommandSender sender) {
        Util.sendMsg(sender, "&c The command you are looking for does not exist");
    }

    public void onNoPermission(CommandSender sender) {
        Util.sendMsg(sender, "&c You do not have the permission to execute this command");
    }
}
