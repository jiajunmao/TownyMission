package world.naturecraft.townymission.core.components.entity;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class PluginMessage {

    private UUID playerUUID;
    private String channel;
    private UUID messageUUID;
    private int size;
    private String data[];

    public PluginMessage(UUID playerUUID, String channel, UUID messageUUID, int size, String[] data) {
        this.playerUUID = playerUUID;
        this.channel = channel;
        this.messageUUID = messageUUID;
        this.size = size;
        this.data = data;
    }

    public PluginMessage() {}

    public PluginMessage playerUUID(UUID id) {
        this.playerUUID = id;
        return this;
    }

    public PluginMessage channel(String channel) {
        this.channel = channel;
        return this;
    }

    public PluginMessage messageUUID(UUID id) {
        this.messageUUID = id;
        return this;
    }

    public PluginMessage dataSize(int size) {
        this.size = size;
        return this;
    }

    public PluginMessage data(String[] data) {
        this.data = data;
        return this;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getChannel() {
        return channel;
    }

    public UUID getMessageUUID() {
        return messageUUID;
    }

    public int getSize() {
        return size;
    }

    public String[] getData() {
        return data;
    }
}
