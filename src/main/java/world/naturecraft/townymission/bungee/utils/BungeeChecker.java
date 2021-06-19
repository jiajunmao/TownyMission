package world.naturecraft.townymission.bungee.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.UUID;

/**
 * The type Bungee checker.
 */
public class BungeeChecker {

    /**
     * Is mayor boolean.
     *
     * @param player the player
     * @return the boolean
     */
    public static boolean isMayor(ProxiedPlayer player) {
        return booleanCheck(player, "isMayor");
    }

    /**
     * Has town boolean.
     *
     * @param player the player
     * @return the boolean
     */
    public static boolean hasTown(ProxiedPlayer player) {
        return booleanCheck(player, "hasTown");
    }

    /**
     * Has permission boolean.
     *
     * @param player     the player
     * @param permission the permission
     * @return the boolean
     */
    public static boolean hasPermission(ProxiedPlayer player, String permission) {
        UUID uuid = UUID.randomUUID();
        // Check whether has town
        PluginMessage request = new PluginMessage(
                player.getUniqueId(),
                "sanitycheck:request",
                uuid,
                1,
                new String[]{"hasPermission", permission}
        );

        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request);
        // We know that data[0] should contain the boolean response
        return Boolean.parseBoolean(response.getData()[0]);
    }

    private static boolean booleanCheck(ProxiedPlayer player, String check) {
        UUID uuid = UUID.randomUUID();
        // Check whether has town
        PluginMessage request = new PluginMessage(
                player.getUniqueId(),
                "sanitycheck:request",
                uuid,
                1,
                new String[]{check}
        );

        // Registering CompletableFuture to get response
        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request);
        // We know that data[0] should contain the boolean response
        return Boolean.parseBoolean(response.getData()[0]);
    }
}
