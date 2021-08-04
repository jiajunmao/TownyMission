package world.naturecraft.townymission.services;

import org.bukkit.Bukkit;
import world.naturecraft.naturelib.InstanceType;

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
            if (InstanceType.isBukkit()) {
                String packageName = EconomyService.class.getPackage().getName();
                String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                try {
                    singleton = (EconomyService) Class.forName(packageName + "." + "EconomyBukkitService").newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
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
