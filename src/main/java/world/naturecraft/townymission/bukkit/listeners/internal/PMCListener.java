package world.naturecraft.townymission.bukkit.listeners.internal;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.services.EconomyBukkitService;
import world.naturecraft.townymission.bukkit.services.PlayerBukkitService;
import world.naturecraft.townymission.bukkit.services.TownyBukkitService;
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

        switch (subchannel) {
            case "sanitycheck:request":
                handleSanityCheck(request);
                break;
            case "townyinfo:request":
                handleTownyInfo(request);
                break;
            case "player:reqeust":
                handlePlayer(request);
                break;
            case "econ:request":
                handleEconomy(request);
                break;
        }
    }

    /**
     * Handle economy.
     *
     * @param request the request
     */
    public void handleEconomy(PluginMessage request) {
        String[] requestData = request.getData();
        String[] responseData = null;
        int responseSize = 0;

        switch (requestData[0]) {
            case "getBalance":
                responseSize = 1;
                responseData = new String[responseSize];
                responseData[0] = String.valueOf(EconomyBukkitService.getInstance().getBalance(request.getPlayerUUID()));
                break;
            case "depositBalance":
                EconomyBukkitService.getInstance().depositBalance(request.getPlayerUUID(), Double.parseDouble(requestData[1]));
                responseSize = 1;
                responseData = new String[responseSize];
                responseData[0] = "success";
                break;
            case "withdrawBalance":
                EconomyBukkitService.getInstance().withdrawBalance(request.getPlayerUUID(), Double.parseDouble(requestData[1]));
                responseSize = 1;
                responseData = new String[responseSize];
                responseData[0] = "success";
                break;
        }

        PluginMessagingService.sendData(new PluginMessage(
                request.getPlayerUUID(),
                "econ:response",
                request.getMessageUUID(),
                responseSize,
                responseData
        ));
    }

    /**
     * Handle player.
     *
     * @param request the request
     */
    public void handlePlayer(PluginMessage request) {
        String[] requestData = request.getData();
        String[] responseData = null;
        int responseSize = 0;

        switch (requestData[0]) {
            case "getNumEmptySlot":
                Player player = Bukkit.getPlayer(responseData[1]);
                responseSize = 1;
                int numEmpty = PlayerBukkitService.getInstance().getNumEmptySlot(request.getPlayerUUID());
                responseData = new String[responseSize];
                responseData[0] = String.valueOf(numEmpty);
                break;
            case "addItem":
                PlayerBukkitService.getInstance().addItem(request.getPlayerUUID(), requestData[0], Integer.parseInt(requestData[1]));
                responseSize = 1;
                responseData = new String[responseSize];
                responseData[0] = "success";
                break;
        }

        PluginMessagingService.sendData(new PluginMessage(
                request.getPlayerUUID(),
                "player:response",
                request.getMessageUUID(),
                responseSize,
                responseData
        ));
    }

    /**
     * Handle towny info.
     *
     * @param request the request
     */
    public void handleTownyInfo(PluginMessage request) {
        String[] requestData = request.getData();
        String[] responseData = null;
        int responseSize = 0;
        switch (requestData[0]) {
            // data[0] is the player UUID
            case "getTownOfPlayer":
                UUID residentOf = TownyBukkitService.getInstance().residentOf(request.getPlayerUUID());
                if (residentOf == null) {
                    responseSize = 0;
                    responseData = null;
                } else {
                    responseSize = 1;
                    responseData = new String[responseSize];
                    responseData[0] = residentOf.toString();
                }
                break;
            case "getMayorOfPlayer":
                UUID mayorOf = TownyBukkitService.getInstance().mayorOf(request.getPlayerUUID());
                if (mayorOf == null) {
                    responseSize = 0;
                    responseData = null;
                } else {
                    responseSize = 1;
                    responseData = new String[responseSize];
                    responseData[0] = mayorOf.toString();
                }
                break;
            case "getTownName":
                Player player = Bukkit.getPlayer(request.getPlayerUUID());
                Town town = TownyUtil.residentOf(player);
                if (town == null) {
                    responseSize = 0;
                    responseData = null;
                } else {
                    responseSize = 1;
                    responseData = new String[responseSize];
                    responseData[0] = town.getName();
                }
                break;
            // data[0] is the town UUID
            case "getTownResidents":
                player = Bukkit.getPlayer(request.getPlayerUUID());
                town = TownyUtil.residentOf(player);

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
                request.getPlayerUUID(),
                "townyinfo:response",
                request.getMessageUUID(),
                responseSize,
                responseData
        ));
    }

    /**
     * Handle sanity check.
     *
     * @param request the request
     */
    public void handleSanityCheck(PluginMessage request) {
        TownyMissionBukkit townyMissionInstance = TownyMissionInstance.getInstance();
        String[] requestData = request.getData();
        Player player = Bukkit.getPlayer(request.getPlayerUUID());
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
                request.getMessageUUID(), 1,
                new String[]{String.valueOf(result)}
        ));
    }
}
