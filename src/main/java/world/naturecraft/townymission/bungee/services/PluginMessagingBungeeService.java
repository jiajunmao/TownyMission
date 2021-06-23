package world.naturecraft.townymission.bungee.services;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.services.PluginMessagingBukkitService;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

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


    public void send(PluginMessage message) {
        TownyMissionInstance instance = TownyMissionInstance.getInstance();
        String mainSrvStr = instance.getInstanceConfig().getString("main-server");
        ServerInfo info = ProxyServer.getInstance().getServerInfo(mainSrvStr);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(message.getChannel());
        out.writeUTF(message.getMessageUUID().toString());
        out.writeInt(message.getSize());
        for (String str : message.getData()) {
            out.writeUTF(str);
        }

        info.sendData("townymission:main", out.toByteArray());
    }
}
