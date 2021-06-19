package world.naturecraft.townymission.bungee.services;

import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.EconomyService;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.UUID;

/**
 * The type Economy bungee service.
 */
public class EconomyBungeeService extends EconomyService {

    private static EconomyBungeeService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
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
                1,
                new String[]{"getBalance"});
        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request);
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
                new String[]{"depositBalance", String.valueOf(amount)});
        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request);
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
                new String[]{"withdrawBalance", String.valueOf(amount)});
        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request);
    }
}
