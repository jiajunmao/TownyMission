package world.naturecraft.townymission.core.components.entity;

import java.util.UUID;

/**
 * The type Plugin message.
 */
public class PluginMessage {

    private UUID playerUUID;
    private String channel;
    private UUID messageUUID;
    private int size;
    private String[] data;

    /**
     * Instantiates a new Plugin message.
     *
     * @param playerUUID  the player uuid
     * @param channel     the channel
     * @param messageUUID the message uuid
     * @param size        the size
     * @param data        the data
     */
    public PluginMessage(UUID playerUUID, String channel, UUID messageUUID, int size, String[] data) {
        this.playerUUID = playerUUID;
        this.channel = channel;
        this.messageUUID = messageUUID;
        this.size = size;
        this.data = data;
    }

    /**
     * Instantiates a new Plugin message.
     */
    public PluginMessage() {}

    /**
     * Player uuid plugin message.
     *
     * @param id the id
     * @return the plugin message
     */
    public PluginMessage playerUUID(UUID id) {
        this.playerUUID = id;
        return this;
    }

    /**
     * Channel plugin message.
     *
     * @param channel the channel
     * @return the plugin message
     */
    public PluginMessage channel(String channel) {
        this.channel = channel;
        return this;
    }

    /**
     * Message uuid plugin message.
     *
     * @param id the id
     * @return the plugin message
     */
    public PluginMessage messageUUID(UUID id) {
        this.messageUUID = id;
        return this;
    }

    /**
     * Data size plugin message.
     *
     * @param size the size
     * @return the plugin message
     */
    public PluginMessage dataSize(int size) {
        this.size = size;
        return this;
    }

    /**
     * Data plugin message.
     *
     * @param data the data
     * @return the plugin message
     */
    public PluginMessage data(String[] data) {
        this.data = data;
        return this;
    }

    /**
     * Gets player uuid.
     *
     * @return the player uuid
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Gets channel.
     *
     * @return the channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Gets message uuid.
     *
     * @return the message uuid
     */
    public UUID getMessageUUID() {
        return messageUUID;
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Get data string [ ].
     *
     * @return the string [ ]
     */
    public String[] getData() {
        return data;
    }
}
