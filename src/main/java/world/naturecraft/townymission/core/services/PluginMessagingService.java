package world.naturecraft.townymission.core.services;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.services.PluginMessagingBukkitService;
import world.naturecraft.townymission.bungee.services.PluginMessagingBungeeService;
import world.naturecraft.townymission.core.components.entity.PluginMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The type Plugin messaging service.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class PluginMessagingService {

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
            if (TownyMissionInstanceType.isBukkit()) {
                singleton = PluginMessagingBukkitService.getInstance();
            } else {
                singleton = PluginMessagingBungeeService.getInstance();
            }
        }

        return singleton;
    }

    /**
     * Parse data plugin message.
     *
     * @param byteData the data
     * @return the plugin message
     */
    public static PluginMessage parseData(Byte[] byteData) {
        byte[] data = new byte[byteData.length];
        for (int i = 0; i < byteData.length; i++) {
            data[i] = byteData[i];
        }

        return parseData(data);
    }

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

    public void completeRequest(String respondId, byte[] data) {
        Byte[] byteData = new Byte[data.length];
        for (int i = 0; i < data.length; i++) {
            byteData[i] = data[i];
        }

        completeRequest(respondId, byteData);
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
    public abstract PluginMessage sendAndWait(PluginMessage message);

    public abstract void send(PluginMessage message);
}
