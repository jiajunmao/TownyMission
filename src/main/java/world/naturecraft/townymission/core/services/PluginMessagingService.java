package world.naturecraft.townymission.core.services;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bungee.TownyMissionBungee;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.components.enums.ServerType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PluginMessagingService {

    Map<String, CompletableFuture<Byte[]>> response;

    public static PluginMessagingService singleton;

    public PluginMessagingService() {
        this.response = new HashMap<>();
    }

    public static PluginMessagingService getInstance() {
        if (singleton == null) {
            singleton = new PluginMessagingService();
        }

        return singleton;
    }

    public static void registerChannel() {
        if (TownyMissionInstance.getInstance() instanceof TownyMissionBungee) {
            TownyMissionBungee townyMissionBungee = TownyMissionInstance.getInstance();
            townyMissionBungee.getProxy().registerChannel("townymission:main");
            townyMissionBungee.getLogger().info("townymission:main PMC channel registered");
        }
    }

    public static void deregisterChannel() {
        if (TownyMissionInstance.getInstance() instanceof TownyMissionBungee) {
            TownyMissionBungee townyMissionBungee = TownyMissionInstance.getInstance();
            townyMissionBungee.getProxy().unregisterChannel("townymission:main");
            townyMissionBungee.getLogger().info("townymission:main PMC channel unregistered");
        }
    }

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

    public CompletableFuture<Byte[]> registerRequest(String respondId) {
        CompletableFuture<Byte[]> future = new CompletableFuture<>();
        this.response.put(respondId, future);

        return future;
    }

    public void completeRequest(String responseId, Byte[] data) {
        this.response.get(responseId).complete(data);
    }

    public CompletableFuture<Byte[]> getFuture(String responseId) {
        return this.response.get(responseId);
    }

    public PluginMessage sendAndWaitForResponse(PluginMessage message) {
        getInstance().registerRequest(message.getMessageUUID().toString());
        Byte[] responseBytes = getInstance().getFuture(message.getMessageUUID().toString()).join();
        PluginMessage response = parseData(responseBytes);
        return response;
    }
}
