package world.naturecraft.townymission.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.TownyMissionBungee;
import world.naturecraft.townymission.components.entity.PluginMessage;
import world.naturecraft.townymission.services.PluginMessagingService;

import java.util.UUID;

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

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        UUID msgUUID = UUID.fromString(in.readUTF());
        int requestSize = in.readInt();
        String[] data = new String[requestSize];
        for (int i = 0; i < requestSize; i++) {
            data[i] = in.readUTF();
        }

        System.out.println("Received message " + msgUUID);
        TownyMissionBungee instance = TownyMissionInstance.getInstance();
        if (subChannel.equals("config:request")) {
            String configValue = instance.getConfig().getString(data[0]);
            PluginMessage response = new PluginMessage()
                    .channel("config:response")
                    .messageUUID(msgUUID)
                    .dataSize(1)
                    .data(new String[]{configValue});

            PluginMessagingService.getInstance().send(response);
        }
    }
}
