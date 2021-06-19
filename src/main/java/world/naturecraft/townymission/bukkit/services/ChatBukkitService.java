package world.naturecraft.townymission.bukkit.services;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
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


    public void sendMsg(UUID playerUUID, String message) {
        Player player = Bukkit.getPlayer(playerUUID);
        BukkitUtil.sendMsg(playerUUID, message);
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
