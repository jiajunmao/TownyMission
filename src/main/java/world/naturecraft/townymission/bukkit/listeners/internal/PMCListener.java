package world.naturecraft.townymission.bukkit.listeners.internal;

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
        UUID uuid = request.getMessageUUID();
        int size = request.getSize();
        String[] args = request.getData();

        if (subchannel.equalsIgnoreCase("sanitycheck:request")) {
            TownyMissionBukkit townyMissionInstance = TownyMissionInstance.getInstance();
            boolean result;
            switch (args[0]) {
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
                            .hasPermission(args[1]).check();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + args[0]);
            }

            PluginMessagingService.sendData(new PluginMessage(
                    player.getUniqueId(),
                    "sanitycheck:response",
                    uuid, 1,
                    new String[]{String.valueOf(result)}
            ));
        } else if (subchannel.equalsIgnoreCase("data:request")) {
            String[] data = null;
            int responseSize = 0;
            switch (subchannel) {
                // data[0] is the player UUID
                case "getTownUUID":
                    responseSize = 1;
                    data = new String[size];
                    data[0] = TownyUtil.residentOf(Bukkit.getPlayer(UUID.fromString(data[0]))).getUUID().toString();
                    break;
                // data[0] is the player UUID
                case "getTownName":
                    responseSize = 1;
                    data = new String[size];
                    data[0] = TownyUtil.residentOf(Bukkit.getPlayer(UUID.fromString(data[0]))).getName();
                    break;
            }

            PluginMessagingService.sendData(new PluginMessage(
                    player.getUniqueId(),
                    "sanitycheck:response",
                    uuid, responseSize,
                    data
            ));
        }
    }
}
