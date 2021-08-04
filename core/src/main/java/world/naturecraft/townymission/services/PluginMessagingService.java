package world.naturecraft.townymission.services;

import world.naturecraft.naturelib.InstanceType;
import world.naturecraft.townymission.components.PluginMessage;

import java.util.HashMap;
import java.util.Map;
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

                if (InstanceType.isBukkit()) {
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

    public PluginMessage sendAndWait(PluginMessage message) {
        CompletableFuture<Byte[]> future = getInstance().registerRequest(message.getMessageUUID().toString());
        send(message);

        Byte[] responseBytes = future.join();
        response.remove(message.getMessageUUID().toString());
        return PluginMessage.parse(responseBytes);
    }

    public PluginMessage sendAndWait(PluginMessage message, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Byte[]> future = getInstance().registerRequest(message.getMessageUUID().toString());
        send(message);

        Byte[] responseBytes = future.get(timeout, unit);
        response.remove(message.getMessageUUID().toString());
        return PluginMessage.parse(responseBytes);
    }

    public abstract void send(PluginMessage message);
}
