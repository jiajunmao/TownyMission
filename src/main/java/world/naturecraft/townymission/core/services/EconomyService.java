package world.naturecraft.townymission.core.services;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import world.naturecraft.townymission.TownyMissionInstance;

/**
 * The type Economy service.
 */
public class EconomyService extends TownyMissionService {

    private static EconomyService singleton;
    private final Economy economy;

    /**
     * Instantiates a new Economy service.
     */
    public EconomyService() {
        super();
        if (Bukkit.getPluginManager().getPlugin("Vault") == null)
            throw new IllegalStateException("Vault is a hard-dependency!");

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new IllegalStateException("Vault is a hard-dependency!");
        }
        economy = rsp.getProvider();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static EconomyService getInstance() {
        if (singleton == null) {
            singleton = new EconomyService();
        }

        return singleton;
    }

    /**
     * Gets balance.
     *
     * @param player the player
     * @return the balance
     */
    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    /**
     * Deposit balance.
     *
     * @param player the player
     * @param amount the amount
     */
    public void depositBalance(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    /**
     * Withdraw balance.
     *
     * @param player the player
     * @param amount the amount
     */
    public void withdrawBalance(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }
}
