package world.naturecraft.townymission.bungee.services;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.bungee.utils.BungeeChecker;
import world.naturecraft.townymission.core.components.entity.MissionEntry;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.ChatService;
import world.naturecraft.townymission.core.services.MissionService;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.UUID;

public class MissionBungeeService extends MissionService {

    private static MissionBungeeService singletion;

    public static MissionBungeeService getInstance() {
        if (singletion == null) {
            singletion = new MissionBungeeService();
        }

        return singletion;
    }

    /**
     * Can start mission boolean.
     *
     * @param playerUUID the player
     * @return the boolean
     */
    public boolean canStartMission(UUID playerUUID) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerUUID);
        // Send the message to the server
        if (!BungeeChecker.hasTown(proxiedPlayer)) return false;

        if (!BungeeChecker.hasPermission(proxiedPlayer, "townymission.player")) return false;

        UUID uuid = UUID.randomUUID();
        // Check whether has permission
        PluginMessage request = new PluginMessage(
                proxiedPlayer.getUniqueId(),
                "data:request",
                uuid,
                1,
                new String[]{"getTownUUID"}
        );

        PluginMessage response = PluginMessagingService.getInstance().sendAndWaitForResponse(request);
        String townUUID = response.getData()[0];

        if (!hasStarted(townUUID)) {
            return true;
        } else {
            ChatService.getInstance().sendMsg(playerUUID, instance.getLangEntry("commands.start.onAlreadyStarted"));
            return false;
        }
    }

    public boolean canAbortMission(UUID playerUUID, MissionEntry entry) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerUUID);
        // Send the message to the server
        if (!BungeeChecker.hasTown(proxiedPlayer)) return false;

        if (!BungeeChecker.hasPermission(proxiedPlayer, "townymission.player")) return false;

        UUID uuid = UUID.randomUUID();
        // Check whether has permission
        PluginMessage request = new PluginMessage(
                proxiedPlayer.getUniqueId(),
                "data:request",
                uuid,
                1,
                new String[]{"getTownUUID"}
        );

        PluginMessage response = PluginMessagingService.getInstance().sendAndWaitForResponse(request);
        String townUUID = response.getData()[0];

        if (hasStarted(townUUID)) {
            ChatService.getInstance().sendMsg(playerUUID, instance.getLangEntry("commands.start.onAlreadyStarted"));
            return false;
        }

        boolean isMayor = BungeeChecker.isMayor(proxiedPlayer);
        boolean canAbort = entry.getStartedPlayer().getUniqueId().equals(proxiedPlayer.getUniqueId()) || isMayor;

        return canAbort;
    }
}
