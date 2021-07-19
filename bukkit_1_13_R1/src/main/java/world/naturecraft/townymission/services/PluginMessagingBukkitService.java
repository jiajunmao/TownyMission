package world.naturecraft.townymission.services;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.PluginMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PluginMessagingBukkitService extends PluginMessagingService {

    public PluginMessage sendAndWait(PluginMessage message) {
        CompletableFuture<Byte[]> future = getInstance().registerRequest(message.getMessageUUID().toString());
        send(message);

        Byte[] responseBytes = future.join();
        response.remove(message.getMessageUUID().toString());
        return parseData(responseBytes);
    }

    @Override
    public PluginMessage sendAndWait(PluginMessage message, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Byte[]> future = getInstance().registerRequest(message.getMessageUUID().toString());
        send(message);

        Byte[] responseBytes = future.get(timeout, unit);
        response.remove(message.getMessageUUID().toString());
        return parseData(responseBytes);
    }

    public void send(PluginMessage message) {
        // Selecting a random player
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.size() == 0) {
            //TownyMissionInstance.getInstance().getInstanceLogger().warning("No player online, cannot send plugin message");
            return;
        }
        Player player = players.get(0);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(message.getChannel());
        out.writeUTF(message.getMessageUUID().toString());
        out.writeLong(message.getTimestamp());
        out.writeInt(message.getSize());
        for (String str : message.getData()) {
            out.writeUTF(str);
        }

        TownyMissionBukkit instance = TownyMissionInstance.getInstance();
        player.sendPluginMessage(instance, "townymission:main", out.toByteArray());
    }


}
