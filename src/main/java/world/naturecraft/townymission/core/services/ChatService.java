package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.services.ChatBukkitService;
import world.naturecraft.townymission.bungee.services.ChatBungeeService;

import java.util.UUID;

/**
 * The type Chat service.
 */
public abstract class ChatService extends TownyMissionService {

    private static ChatService singleton;

    public static ChatService getInstance() {
        if (singleton == null) {
            if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
                singleton = ChatBukkitService.getInstance();
            } else {
                singleton = ChatBungeeService.getInstance();
            }
        }

        return singleton;
    }

    public abstract void sendMsg(UUID playerUUID, String message);

    public abstract String translateColor(String message);
}
