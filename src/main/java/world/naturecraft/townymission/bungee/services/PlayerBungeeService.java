package world.naturecraft.townymission.bungee.services;

import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.PlayerService;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.UUID;

/**
 * The type Player bungee service.
 */
public class PlayerBungeeService extends PlayerService {
    @Override
    public int getNumEmptySlot(UUID playerUUID) {
        // Send PMC to server to ask
        UUID messageUUID = UUID.randomUUID();
        PluginMessage pluginMessage = new PluginMessage(
                playerUUID,
                "player:request",
                messageUUID,
                2,
                new String[]{"getNumEmptySlot"}
        );

        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(pluginMessage);
        return Integer.parseInt(response.getData()[0]);
    }

    @Override
    public void addItem(UUID playerUUID, String material, int amount) {
        PluginMessage request = new PluginMessage(
                playerUUID,
                "player:request",
                UUID.randomUUID(),
                2,
                new String[]{"addItem", material, String.valueOf(amount)});

        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request);
        // TODO: maybe check for success?
    }
}
