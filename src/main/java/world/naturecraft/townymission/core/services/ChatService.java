package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.services.ChatBukkitService;

import java.util.UUID;

/**
 * The type Chat service.
 */
public abstract class ChatService extends TownyMissionService {

    private static ChatService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ChatService getInstance() {
        if (singleton == null) {
            if (TownyMissionInstanceType.isBukkit()) {
                singleton = ChatBukkitService.getInstance();
            }
        }

        return singleton;
    }

    /**
     * Send msg.
     *
     * @param playerUUID the player uuid
     * @param message    the message
     */
    public abstract void sendMsg(UUID playerUUID, String message);

    /**
     * Translate color string.
     *
     * @param message the message
     * @return the string
     */
    public abstract String translateColor(String message);
}
