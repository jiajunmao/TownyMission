package world.naturecraft.townymission.bungee.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class BungeeUtil {

    public static String getPlayerNameFromUUID(UUID playerUUID) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUUID);
        return player.getName();
    }

    public static void sendMessage(ProxiedPlayer player, String message) {
        player.sendMessage(message);
    }
}
