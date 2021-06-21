package world.naturecraft.townymission.core.services;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import world.naturecraft.townymission.core.components.entity.PluginMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The type Plugin messaging service.
 */
@SuppressWarnings("UnstableApiUsage")
public class PluginMessagingService {

    /**
     * The constant singleton.
     */
    public static PluginMessagingService singleton;
    /**
     * The Response.
     */
    Map<String, CompletableFuture<Byte[]>> response;

    /**
     * Instantiates a new Plugin messaging service.
     */
    public PluginMessagingService() {
        this.response = new HashMap<>();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static PluginMessagingService getInstance() {
        if (singleton == null) {
            singleton = new PluginMessagingService();
        }

        return singleton;
    }

    /**
     * Send data.
     *
     * @param message the message
     */
    public static void sendData(PluginMessage message) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(message.getChannel());
        out.writeUTF(message.getMessageUUID().toString());
        out.writeInt(message.getSize());
        for (String str : message.getData()) {
            out.writeUTF(str);
        }

        ProxyServer.getInstance().getPlayer(message.getPlayerUUID())
                .getServer().getInfo().sendData("townymission:main", out.toByteArray());
    }

    /**
     * Send data.
     *
     * @param message    the message
     * @param serverName the server name
     */
    public static void sendData(PluginMessage message, String serverName) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(message.getChannel());
        out.writeUTF(message.getMessageUUID().toString());
        out.writeInt(message.getSize());
        for (String str : message.getData()) {
            out.writeUTF(str);
        }

        ProxyServer.getInstance().getServerInfo(serverName).sendData("townymission:main", out.toByteArray());
    }

    /**
     * Parse data plugin message.
     *
     * @param byteData the byte data
     * @return the plugin message
     */
    public static PluginMessage parseData(Byte[] byteData) {
        byte[] data = new byte[byteData.length];
        for (int i = 0; i < byteData.length; i++) {
            data[i] = byteData[i];
        }
        return parseData(data);
    }

    /**
     * Parse data plugin message.
     *
     * @param data the data
     * @return the plugin message
     */
    public static PluginMessage parseData(byte[] data) {
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        PluginMessage message = new PluginMessage()
                .channel(in.readUTF())
                .messageUUID(UUID.fromString(in.readUTF()))
                .dataSize(in.readInt());

        int size = message.getSize();
        String[] strData = new String[size];
        for (int i = 0; i < size; i++) {
            strData[i] = in.readUTF();
        }

        message.data(strData);
        return message;
    }

    /**
     * Register request completable future.
     *
     * @param respondId the respond id
     * @return the completable future
     */
    public CompletableFuture<Byte[]> registerRequest(String respondId) {
        CompletableFuture<Byte[]> future = new CompletableFuture<>();
        this.response.put(respondId, future);

        return future;
    }

    /**
     * Complete request.
     *
     * @param responseId the response id
     * @param data       the data
     */
    public void completeRequest(String responseId, Byte[] data) {
        this.response.get(responseId).complete(data);
    }

    /**
     * Gets future.
     *
     * @param responseId the response id
     * @return the future
     */
    public CompletableFuture<Byte[]> getFuture(String responseId) {
        return this.response.get(responseId);
    }

    /**
     * Send and wait for response plugin message.
     *
     * @param message the message
     * @return the plugin message
     */
    public PluginMessage sendAndWait(PluginMessage message) {
        CompletableFuture<Byte[]> future = getInstance().registerRequest(message.getMessageUUID().toString());
        sendData(message);
        Byte[] responseBytes = future.join();
        return parseData(responseBytes);
    }

    /**
     * Send and wait plugin message.
     *
     * @param message    the message
     * @param serverName the server name
     * @return the plugin message
     */
    public PluginMessage sendAndWait(PluginMessage message, String serverName) {
        CompletableFuture<Byte[]> future = getInstance().registerRequest(message.getMessageUUID().toString());
        sendData(message, serverName);
        Byte[] responseBytes = future.join();
        return parseData(responseBytes);
    }
}
