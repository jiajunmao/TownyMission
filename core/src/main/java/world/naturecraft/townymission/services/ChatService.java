package world.naturecraft.townymission.services;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMissionInstanceType;

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
                // packageName = world.naturecraft.townymission.services
                String packageName = ChatService.class.getPackage().getName();
                String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                try {
                    singleton = (ChatService) Class.forName(packageName + "." + "ChatBukkitService").newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
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
