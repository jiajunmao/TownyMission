package world.naturecraft.townymission.bukkit.services;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.core.services.ChatService;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Send msg.
     *
     * @param sender  the sender
     * @param message the message
     */
    public void sendMsg(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(translateColor(message));
        } else {
            sender.getServer().getLogger().info(translateColor(message));
        }
    }

    public void sendMsg(UUID playerUUID, String message) {
        Player player = Bukkit.getPlayer(playerUUID);
        sendMsg(player, message);
    }

    /**
     * Translate hex color string.
     *
     * @param startTag the start tag
     * @param endTag   the end tag
     * @param message  the message
     * @return the string
     */
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

    /**
     * Translate vanilla color string.
     *
     * @param message the message
     * @return the string
     */
    private String translateVanillaColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Translate color string.
     *
     * @param message the message
     * @return the string
     */
    public String translateColor(String message) {
        return translateVanillaColor(translateHexColor("\\{#", "\\}", message));
    }
}
