package world.naturecraft.townymission.services;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import world.naturecraft.townymission.components.PluginMessage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PluginMessagingBungeeService extends PluginMessagingService {

    /**
     * The constant singleton.
     */
    public static PluginMessagingBungeeService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static PluginMessagingBungeeService getInstance() {
        if (singleton == null) {
            singleton = new PluginMessagingBungeeService();
        }

        return singleton;
    }

    /**
     * Send and wait for response plugin message.
     *
     * @param message the message
     * @return the plugin message
     */
    @Override
    public PluginMessage sendAndWait(PluginMessage message) {
        throw new IllegalStateException("Bungeecord should not wait for anything from the server!");
    }

    @Override
    public PluginMessage sendAndWait(PluginMessage message, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        throw new IllegalStateException("Bungeecord should not wait for anything from the server!");
    }


    public void send(PluginMessage message) {
        ServerInfo destination = ProxyServer.getInstance().getServerInfo(message.getDestination());
        destination.sendData("townymission:main", message.asByteArray());
    }
}
