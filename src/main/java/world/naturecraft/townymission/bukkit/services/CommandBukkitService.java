package world.naturecraft.townymission.bukkit.services;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.core.services.CommandService;

import java.util.UUID;

public class CommandBukkitService extends CommandService {

    private static CommandBukkitService singleton;

    public static CommandBukkitService getInstance() {
        if (singleton == null) {
            singleton = new CommandBukkitService();
        }

        return singleton;
    }

    @Override
    public void dispatchCommand(UUID playerUUID, String command) {
        Player player = Bukkit.getPlayer(playerUUID);
        Bukkit.dispatchCommand(player, command);
    }
}
