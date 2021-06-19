package world.naturecraft.townymission.bukkit.listeners.internal;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.List;
import java.util.UUID;

/**
 * The type Pmc listener.
 */
public class PMCListener implements PluginMessageListener {

    /**
     * A method that will be thrown when a PluginMessageSource sends a plugin
     * message on a registered channel.
     *
     * @param channel Channel that the message was sent through.
     * @param player  Source of the message.
     * @param message The raw message that was sent.
     */
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equalsIgnoreCase("townymission:main")) return;

        PluginMessage request = PluginMessagingService.parseData(message);
        String subchannel = request.getChannel();
        UUID requestUUID = request.getMessageUUID();
        int requestSize = request.getSize();
        String[] requestData = request.getData();

        if (subchannel.equalsIgnoreCase("sanitycheck:request")) {
            TownyMissionBukkit townyMissionInstance = TownyMissionInstance.getInstance();
            boolean result;
            switch (requestData[0]) {
                case "hasTown":
                    // This has to be the bukkit instead, this event would only be registered with bukkit
                    result = new BukkitChecker(townyMissionInstance).target(player)
                            .hasTown().check();
                    break;
                case "isMayor":
                    result = new BukkitChecker(townyMissionInstance).target(player)
                            .hasTown().isMayor().check();
                    break;
                case "permission":
                    result = new BukkitChecker(townyMissionInstance).target(player)
                            .hasPermission(requestData[1]).check();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestData[0]);
            }

            PluginMessagingService.sendData(new PluginMessage(
                    player.getUniqueId(),
                    "sanitycheck:response",
                    requestUUID, 1,
                    new String[]{String.valueOf(result)}
            ));

        } else if (subchannel.equalsIgnoreCase("data:request")) {
            String[] responseData = null;
            int responseSize = 0;
            switch (requestData[0]) {
                // data[0] is the player UUID
                case "getTownOfPlayer":
                    responseSize = 1;
                    responseData = new String[requestSize];
                    responseData[0] = TownyUtil.residentOf(Bukkit.getPlayer(UUID.fromString(requestData[1]))).getUUID().toString();
                    break;
                case "getMayorOfPlayer":
                    responseSize = 1;
                    responseData = new String[responseSize];
                    responseData[0] = TownyUtil.mayorOf(Bukkit.getPlayer(UUID.fromString(requestData[1]))).getUUID().toString();
                    break;
                // data[0] is the player UUID
                case "getTownName":
                    responseSize = 1;
                    responseData = new String[requestSize];
                    responseData[0] = TownyUtil.residentOf(Bukkit.getPlayer(UUID.fromString(requestData[1]))).getName();
                    break;
                // data[0] is the town UUID
                case "getTownResidents":
                    Town town;
                    try {
                        town = TownyUtil.getTown(UUID.fromString(requestData[1]));
                    } catch (NotRegisteredException e) {
                        town = null;
                    }

                    if (town == null) {
                        responseSize = 0;
                        responseData = null;
                        break;
                    } else {
                        List<Resident> residents = town.getResidents();
                        responseSize = residents.size();
                        responseData = new String[residents.size()];
                        int index = 0;
                        for (Resident resident : residents) {
                            responseData[++index] = resident.getUUID().toString();
                        }
                    }
                    break;
            }

            PluginMessagingService.sendData(new PluginMessage(
                    player.getUniqueId(),
                    "data:response",
                    requestUUID, responseSize,
                    responseData
            ));
        }
    }
}
