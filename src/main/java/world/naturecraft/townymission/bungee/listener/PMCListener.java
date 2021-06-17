package world.naturecraft.townymission.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import org.bukkit.event.EventHandler;

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
    public void on(PluginMessageEvent event) {

        if (!event.getTag().equalsIgnoreCase("townymission:main")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        if (subChannel.equals("mission")) {
            String data = in.readUTF();
            System.out.println("Message received in " + subChannel + " as " + data);
            if (event.getReceiver() instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
            } else if (event.getReceiver() instanceof Server) {
                Server server = (Server) event.getReceiver();
            }
        }
    }
}
