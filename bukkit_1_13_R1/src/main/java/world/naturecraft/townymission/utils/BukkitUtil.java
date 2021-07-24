package world.naturecraft.townymission.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Util.
 */
public class BukkitUtil {


    /**
     * Gets num empty slots in inventory.
     *
     * @param playerInventory the player inventory
     * @return the num empty slots in inventory
     */
    public static int getNumEmptySlotsInInventory(PlayerInventory playerInventory) {
        int num = 0;
        for (int i = 0; i < 36; i++) {
            if (playerInventory.getStorageContents()[i] == null
                    || playerInventory.getStorageContents()[i].getType().equals(Material.AIR)) {
                num++;
            }
        }

        return num;
    }

    /**
     * Gets player name from uuid.
     *
     * @param playerUUID the player uuid
     * @return the player name from uuid
     */
    public static String getPlayerNameFromUUID(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return player.getName();
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
     * Send msg.
     *
     * @param playerUUID the player uuid
     * @param message    the message
     */
    public static void sendMsg(UUID playerUUID, String message) {
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
    private static String translateHexColor(String startTag, String endTag, String message) {
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
    private static String translateVanillaColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Translate color string.
     *
     * @param message the message
     * @return the string
     */
    public static String translateColor(String message) {
        return translateVanillaColor(translateHexColor("\\{#", "\\}", message));
    }
}
