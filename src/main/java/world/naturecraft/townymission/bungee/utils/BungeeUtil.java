package world.naturecraft.townymission.bungee.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * The type Bungee util.
 */
public class BungeeUtil {

    /**
     * Gets player name from uuid.
     *
     * @param playerUUID the player uuid
     * @return the player name from uuid
     */
    public static String getPlayerNameFromUUID(UUID playerUUID) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUUID);
        return player.getName();
    }

    /**
     * Send message.
     *
     * @param player  the player
     * @param message the message
     */
    public static void sendMessage(ProxiedPlayer player, String message) {
        player.sendMessage(message);
    }
}
