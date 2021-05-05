package world.naturecraft.townymission.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Util.
 */
public class Util {

    /**
     * Gets db name.
     *
     * @param dbType the db type
     * @return the db name
     */
    public static String getDbName(DbType dbType) {
        return "townymission_" + dbType.name().toLowerCase(Locale.ROOT);
    }

    /**
     * Send msg.
     *
     * @param sender  the sender
     * @param message the message
     */
    public static void sendMsg(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(translateColor(message));
        } else {
            sender.getServer().getLogger().info(translateColor(message));
        }

    }

    /**
     * Translate hex color string.
     *
     * @param startTag the start tag
     * @param endTag   the end tag
     * @param message  the message
     * @return the string
     */
    public static String translateHexColor(String startTag, String endTag, String message) {
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
    public static String translateVanillaColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String translateColor(String message) {
        return translateVanillaColor(translateHexColor("\\{#", "\\}", message));
    }
}
