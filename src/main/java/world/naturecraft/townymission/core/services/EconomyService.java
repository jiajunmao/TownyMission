package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.services.EconomyBukkitService;

import java.util.UUID;

/**
 * The type Economy service.
 */
public abstract class EconomyService extends TownyMissionService {

    private static EconomyService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static EconomyService getInstance() {
        if (singleton == null) {
            if (TownyMissionInstanceType.isBukkit()) {
                singleton = EconomyBukkitService.getInstance();
            }
        }

        return singleton;
    }

    /**
     * Gets balance.
     *
     * @param playerUUID the player
     * @return the balance
     */
    public abstract double getBalance(UUID playerUUID);

    /**
     * Deposit balance.
     *
     * @param playerUUID the player
     * @param amount     the amount
     */
    public abstract void depositBalance(UUID playerUUID, double amount);

    /**
     * Withdraw balance.
     *
     * @param playerUUID the player
     * @param amount     the amount
     */
    public abstract void withdrawBalance(UUID playerUUID, double amount);
}
