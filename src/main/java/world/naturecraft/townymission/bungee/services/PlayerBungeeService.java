package world.naturecraft.townymission.bungee.services;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.plugin.Plugin;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.PlayerService;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.UUID;

public class PlayerBungeeService extends PlayerService {
    @Override
    public int getNumEmptySlot(UUID playerUUID) {
        // Send PMC to server to ask
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUUID);
        UUID messageUUID = UUID.randomUUID();
        PluginMessage pluginMessage = new PluginMessage(
                playerUUID,
                "data:request",
                messageUUID,
                1,
                new String[]{"getNumEmptySlot"}
        );

        PluginMessage response = PluginMessagingService.getInstance().sendAndWaitForResponse(pluginMessage);
        return Integer.parseInt(response.getData()[0]);
    }

    @Override
    public void addItem(UUID playerUUID, String material, int amount) {
        PluginMessage request = new PluginMessage(
                playerUUID,
                "data:request",
                UUID.randomUUID(),
                2,
                new String[]{material, String.valueOf(amount)});

        PluginMessage response = PluginMessagingService.getInstance().sendAndWaitForResponse(request);
        // TODO: maybe check for success?
    }
}
