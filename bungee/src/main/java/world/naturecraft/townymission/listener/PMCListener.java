package world.naturecraft.townymission.listener;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import world.naturecraft.townymission.TownyMissionBungee;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.PluginMessage;
import world.naturecraft.townymission.services.PluginMessagingService;

/**
 * The type Pmc listener.
 */
public class PMCListener implements Listener {

    /**
     * On.
     *
     * @param event the event
     */
    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("townymission:main")) return;
        PluginMessage message = PluginMessage.parse(event.getData());

        //TownyMissionInstance.getInstance().getInstanceLogger().info("Bungee received message " + message.getMessageUUID());
        TownyMissionBungee instance = TownyMissionInstance.getInstance();
        if (message.getChannel().equals("config:request")) {
            String configValue = instance.getInstanceConfig().getString(message.getData()[0]);
            PluginMessage response = new PluginMessage()
                    .origin(message.getOrigin())
                    .destination(message.getDestination())
                    .channel("config:response")
                    .messageUUID(message.getMessageUUID())
                    .dataSize(1)
                    .data(new String[]{configValue});

            PluginMessagingService.getInstance().send(response);
        } else if (message.getChannel().equals("mission:request")) {
            PluginMessagingService.getInstance().send(message);
        } else if (message.getChannel().equals("mission:response")) {
            PluginMessagingService.getInstance().send(message);
        }

        event.setCancelled(true);
    }
}
