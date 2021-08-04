package world.naturecraft.townymission.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.PluginMessage;

import java.util.ArrayList;
import java.util.List;

public class PluginMessagingBukkitService extends PluginMessagingService {

    public void send(PluginMessage message) {
        // Selecting a random player
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.size() == 0) {
            //TownyMissionInstance.getInstance().getInstanceLogger().warning("No player online, cannot send plugin message");
            return;
        }
        Player player = players.get(0);

        TownyMissionBukkit instance = TownyMissionInstance.getInstance();
        player.sendPluginMessage(instance, "townymission:main", message.asByteArray());
    }

}
