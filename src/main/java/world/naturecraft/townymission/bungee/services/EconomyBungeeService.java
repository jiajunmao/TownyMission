package world.naturecraft.townymission.bungee.services;

import org.bukkit.plugin.Plugin;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.EconomyService;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.UUID;

public class EconomyBungeeService extends EconomyService {

    private static EconomyBungeeService singleton;

    public static EconomyBungeeService getInstance() {
        if (singleton == null) {
            singleton = new EconomyBungeeService();
        }

        return singleton;
    }

    /**
     * Gets balance.
     *
     * @param playerUUID the player
     * @return the balance
     */
    @Override
    public double getBalance(UUID playerUUID) {
        PluginMessage request = new PluginMessage(
                playerUUID,
                "econ:request",
                UUID.randomUUID(),
                2,
                new String[]{"getBalance", playerUUID.toString()});
        PluginMessage response = PluginMessagingService.getInstance().sendAndWaitForResponse(request);
        return Integer.parseInt(response.getData()[0]);
    }

    /**
     * Deposit balance.
     *
     * @param playerUUID the player
     * @param amount     the amount
     */
    @Override
    public void depositBalance(UUID playerUUID, double amount) {
        PluginMessage request = new PluginMessage(
                playerUUID,
                "econ:request",
                UUID.randomUUID(),
                2,
                new String[]{"depositBalance", playerUUID.toString(), String.valueOf(amount)});
        PluginMessage response = PluginMessagingService.getInstance().sendAndWaitForResponse(request);
    }

    /**
     * Withdraw balance.
     *
     * @param playerUUID the player
     * @param amount     the amount
     */
    @Override
    public void withdrawBalance(UUID playerUUID, double amount) {
        PluginMessage request = new PluginMessage(
                playerUUID,
                "econ:request",
                UUID.randomUUID(),
                2,
                new String[]{"withdrawBalance", playerUUID.toString(), String.valueOf(amount)});
        PluginMessage response = PluginMessagingService.getInstance().sendAndWaitForResponse(request);
    }
}
