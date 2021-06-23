package world.naturecraft.townymission.bukkit.services;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PluginMessagingBukkitService extends PluginMessagingService {

    /**
     * The constant singleton.
     */
    public static PluginMessagingBukkitService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static PluginMessagingBukkitService getInstance() {
        if (singleton == null) {
            singleton = new PluginMessagingBukkitService();
        }

        return singleton;
    }

    public PluginMessage sendAndWait(PluginMessage message) {
        CompletableFuture<Byte[]> future = getInstance().registerRequest(message.getMessageUUID().toString());

        send(message);

        Byte[] responseBytes = future.join();
        PluginMessage response = parseData(responseBytes);
        return response;
    }

    public void send(PluginMessage message) {
        // Selecting a random player
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.size() == 0) {
            TownyMissionInstance.getInstance().getInstanceLogger().warning("No player online, cannot send plugin message");
            return;
        }
        Player player = players.get(0);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(message.getChannel());
        out.writeUTF(message.getMessageUUID().toString());
        out.writeInt(message.getSize());
        for (String str : message.getData()) {
            out.writeUTF(str);
        }

        TownyMissionBukkit instance = TownyMissionInstance.getInstance();
        player.sendPluginMessage(instance, "townymission:main", out.toByteArray());
    }
}
