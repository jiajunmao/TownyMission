package world.naturecraft.townymission.services;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.components.PluginMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The type Plugin messaging service.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class PluginMessagingService extends TownyMissionService {

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
            try {
                String packageName = PluginMessagingService.class.getPackage().getName();

                if (TownyMissionInstanceType.isBukkit()) {
                    singleton = (PluginMessagingService) Class.forName(packageName + "." + "PluginMessagingBukkitService").newInstance();
                } else {
                    singleton = (PluginMessagingService) Class.forName(packageName + "." + "PluginMessagingBungeeService").newInstance();
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
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
                .timestamp(in.readLong())
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
        //instance.getInstanceLogger().info("Registering " + respondId);
        CompletableFuture<Byte[]> future = new CompletableFuture<>();
        response.put(respondId, future);

        return future;
    }

    /**
     * Complete request.
     *
     * @param responseId the response id
     * @param data       the data
     */
    public void completeRequest(String responseId, Byte[] data) {
        if (!response.containsKey(responseId)) return;
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

    public abstract PluginMessage sendAndWait(PluginMessage message, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException;

    public abstract void send(PluginMessage message);
}
