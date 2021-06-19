package world.naturecraft.townymission.bungee.services;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.core.services.ChatService;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatBungeeService extends ChatService {

    public static ChatBungeeService singleton;

    public static ChatBungeeService getInstance() {
        if (singleton == null) {
            singleton = new ChatBungeeService();
        }

        return singleton;
    }

    public void sendMsg(UUID playerUUID, String message) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUUID);
        player.sendMessage(translateColor(message));
    }

    private String translateVanillaColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String translateHexColor(String startTag, String endTag, String message) {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        char COLOR_CHAR = ChatColor.COLOR_CHAR;
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

    public String translateColor(String message) {
        return translateVanillaColor(translateHexColor("\\{#", "\\}", message));
    }
}
