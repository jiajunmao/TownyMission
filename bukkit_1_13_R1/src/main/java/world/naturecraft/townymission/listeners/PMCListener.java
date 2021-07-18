package world.naturecraft.townymission.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.PluginMessage;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.services.PluginMessagingService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.Date;
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

        instance.getInstanceLogger().info("Received PMC on townymission:main");
        // Ignore if the message has been sent 3 seconds ago, 5s is the mark for other server to cache
        long timeDiff = new Date().getTime() - request.getTimestamp();
        if (Util.msToS(timeDiff) > 3) {
            instance.getInstanceLogger().info("PMC older than 3 seconds, disgarding. Time diff: " + Util.msToS(timeDiff) + "s");
            return;
        }

        String subchannel = request.getChannel();

        if (subchannel.equalsIgnoreCase("config:response")) {
            PluginMessagingService.getInstance().completeRequest(request.getMessageUUID().toString(), message);
        } else if (subchannel.equalsIgnoreCase("mission:request")) {
            // If this is not the main server, just ignore, otherwise this infinite looping
            if (!instance.getConfig().getBoolean("bungeecord.main-server")) return;

            OfflinePlayer realPlayer = Bukkit.getOfflinePlayer(UUID.fromString(request.getData()[1]));
            UUID townUUID = TownyUtil.residentOf(realPlayer).getUUID();
            instance.getInstanceLogger().info("Received PMC request for player: " + realPlayer.getName() + " of mission type " + request.getData()[2]);
            // Sanity check

            BukkitChecker checker = new BukkitChecker(instance).target(realPlayer).silent(true)
                    .hasTown()
                    .hasStarted()
                    .isMissionType(MissionType.valueOf(request.getData()[2].toUpperCase(Locale.ROOT)));

            if (!checker.check()) {
                // If not passing sanity check, return false
                PluginMessage response = new PluginMessage()
                        .channel("mission:response")
                        .messageUUID(request.getMessageUUID())
                        .dataSize(1)
                        .data(new String[]{"false"});
                PluginMessagingService.getInstance().send(response);
            }

            MissionService.getInstance().doMission(townUUID, realPlayer.getUniqueId(), MissionType.valueOf(request.getData()[2].toUpperCase(Locale.ROOT)), Integer.parseInt(request.getData()[3]));

            PluginMessage response = new PluginMessage()
                    .channel("mission:response")
                    .messageUUID(request.getMessageUUID())
                    .dataSize(1)
                    .data(new String[]{"true"});
            PluginMessagingService.getInstance().send(response);
        } else if (subchannel.equalsIgnoreCase("mission:response")) {
            // This means we have got response for our deposit command
            PluginMessagingService.getInstance().completeRequest(request.getMessageUUID().toString(), message);
        }
    }
}
