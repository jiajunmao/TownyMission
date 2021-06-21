package world.naturecraft.townymission.bukkit.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.core.services.CommandService;

import java.util.UUID;

/**
 * The type Command bukkit service.
 */
public class CommandBukkitService extends CommandService {

    private static CommandBukkitService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
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
