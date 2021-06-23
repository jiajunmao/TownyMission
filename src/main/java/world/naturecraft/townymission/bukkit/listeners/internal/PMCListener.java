package world.naturecraft.townymission.bukkit.listeners.internal;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.PluginMessagingService;

/**
 * The type Pmc listener.
 */
public class PMCListener implements PluginMessageListener {

    /**
     * A method that will be thrown when a PluginMessageSource sends a plugin
     * message on a registered channel.
     *
     * @param channel Channel that the message was sent through.
     * @param player  Source of the message.
     * @param message The raw message that was sent.
     */
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {

        if (!channel.equalsIgnoreCase("townymission:main")) return;

        PluginMessage request = PluginMessagingService.parseData(message);
        String subchannel = request.getChannel();

        if (subchannel.equalsIgnoreCase("config:response")) {
            System.out.println("Received response on config:response, completing request");
            PluginMessagingService.getInstance().completeRequest(request.getMessageUUID().toString(), message);
        }
    }
}
