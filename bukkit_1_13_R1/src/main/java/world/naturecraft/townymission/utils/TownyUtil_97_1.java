package world.naturecraft.townymission.utils;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.command.TownyCommand;
import org.bukkit.command.CommandExecutor;

public class TownyUtil_97_1 {

    public static void registerSubCommand(TownyCommandAddonAPI.CommandType commandType, String commandName, CommandExecutor commandExecutor) {
        TownyCommandAddonAPI.addSubCommand(commandType, commandName, commandExecutor);
    }
}
