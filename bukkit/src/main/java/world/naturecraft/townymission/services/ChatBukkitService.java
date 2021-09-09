package world.naturecraft.townymission.services;

import org.bukkit.Bukkit;
import world.naturecraft.naturelib.utils.BukkitUtil;

import java.util.UUID;

/**
 * The type Chat bukkit service.
 */
public class ChatBukkitService extends ChatService {

    private static ChatBukkitService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ChatBukkitService getInstance() {
        if (singleton == null) {
            singleton = new ChatBukkitService();
        }

        return singleton;
    }


    public void sendMsg(UUID playerUUID, String message) {
        BukkitUtil.sendMsg(playerUUID, translateColor(message));
    }

    @Override
    public void sendConsoleMsg(String message) {
        Bukkit.getConsoleSender().sendMessage(translateColor(message));
    }

    /**
     * Translate color string.
     *
     * @param message the message
     * @return the string
     */
    @Override
    public String translateColor(String message) {
        return BukkitUtil.translateColor(message);
    }
}
