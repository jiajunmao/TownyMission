package world.naturecraft.townymission.components;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.Date;
import java.util.UUID;

/**
 * The type Plugin message.
 */
public class PluginMessage {

    private String channel;
    private long timestamp;
    private UUID messageUUID;
    private int size;
    private String originSrv;
    private String destinationSrv;
    private String[] data;

    /**
     * Instantiates a new Plugin message.
     *
     * @param channel     the channel
     * @param messageUUID the message uuid
     * @param size        the size
     * @param data        the data
     */
    public PluginMessage(String channel, UUID messageUUID, int size, String[] data) {
        this.channel = channel;
        this.messageUUID = messageUUID;
        this.size = size;
        this.data = data;
        // Create the timestamp this message is created
        timestamp = new Date().getTime();
    }

    /**
     * Instantiates a new Plugin message.
     */
    public PluginMessage() {
        timestamp = new Date().getTime();
    }

    public static PluginMessage parse(byte[] byteArray) {
        ByteArrayDataInput in = ByteStreams.newDataInput(byteArray);
        PluginMessage message = new PluginMessage()
                .origin(in.readUTF())
                .destination(in.readUTF())
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

    public static PluginMessage parse(Byte[] byteArray) {
        byte[] array = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            array[i] = byteArray[i];
        }

        return parse(array);
    }

    public PluginMessage origin(String originSrv) {
        this.originSrv = originSrv;
        return this;
    }

    public PluginMessage destination(String destinationSrv) {
        this.destinationSrv = destinationSrv;
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

    public PluginMessage timestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public long getTimestamp() {
        return timestamp;
    }

    public String getOrigin() {
        return this.originSrv;
    }

    public String getDestination() {
        return this.destinationSrv;
    }

    public byte[] asByteArray() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(originSrv);
        out.writeUTF(destinationSrv);
        out.writeUTF(channel);
        out.writeUTF(messageUUID.toString());
        out.writeLong(timestamp);
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            out.writeUTF(data[i]);
        }

        return out.toByteArray();
    }

}
