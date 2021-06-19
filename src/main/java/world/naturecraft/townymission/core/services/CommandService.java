package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.services.CommandBukkitService;
import world.naturecraft.townymission.bungee.services.CommandBungeeService;

import java.util.UUID;

public abstract class CommandService extends TownyMissionService {

    private static CommandService singleton;

    public static CommandService getInstance() {
        if (singleton == null) {
            if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
                singleton = CommandBukkitService.getInstance();
            } else {
                singleton = CommandBungeeService.getInstance();
            }
        }

        return singleton;
    }

    public abstract void dispatchCommand(UUID playerUUID, String command);
}
