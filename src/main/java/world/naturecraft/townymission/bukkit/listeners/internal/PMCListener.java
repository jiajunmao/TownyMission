package world.naturecraft.townymission.bukkit.listeners.internal;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.services.MissionService;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.Locale;
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

        TownyMissionBukkit instance = TownyMissionInstance.getInstance();
        PluginMessage request = PluginMessagingService.parseData(message);
        String subchannel = request.getChannel();

        if (subchannel.equalsIgnoreCase("config:response")) {
            PluginMessagingService.getInstance().completeRequest(request.getMessageUUID().toString(), message);
        } else if (subchannel.equalsIgnoreCase("mission:response")) {
            // If this is not the main server, just ignore, otherwise this infinite looping
            if (!instance.getConfig().getBoolean("bungeecord.main-server")) return;

            System.out.println("Player UUID is " + request.getData()[1]);
            OfflinePlayer realPlayer = Bukkit.getOfflinePlayer(UUID.fromString(request.getData()[1]));
            UUID townUUID = TownyUtil.residentOf(realPlayer).getUUID();
            System.out.println("Received PMC request for player: " + realPlayer.getName() + " of mission type " + request.getData()[2]);
            System.out.println("The townUUID is " + townUUID);
            MissionService.getInstance().doMission(townUUID, realPlayer.getUniqueId(), MissionType.valueOf(request.getData()[2].toUpperCase(Locale.ROOT)), Integer.parseInt(request.getData()[3]));
        }
    }
}
