package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.services.CommandBukkitService;
import world.naturecraft.townymission.bungee.services.CommandBungeeService;

import java.util.UUID;

/**
 * The type Command service.
 */
public abstract class CommandService extends TownyMissionService {

    private static CommandService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CommandService getInstance() {
        if (singleton == null) {
            if (TownyMissionInstanceType.isBukkit()) {
                singleton = CommandBukkitService.getInstance();
            } else {
                singleton = CommandBungeeService.getInstance();
            }
        }

        return singleton;
    }

    /**
     * Dispatch command.
     *
     * @param playerUUID the player uuid
     * @param command    the command
     */
    public abstract void dispatchCommand(UUID playerUUID, String command);
}
