package world.naturecraft.townymission.bungee.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bungee.TownyMissionBungee;
import world.naturecraft.townymission.bungee.services.PluginMessagingBungeeService;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.PluginMessagingService;

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

        if (subChannel.equals("config:request")) {
            TownyMissionBungee instance = TownyMissionInstance.getInstance();
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
