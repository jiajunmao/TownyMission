package world.naturecraft.townymission.bukkit.services;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import world.naturecraft.townymission.core.services.EconomyService;

import java.util.UUID;

public class EconomyBukkitService extends EconomyService {

    private static EconomyBukkitService singleton;
    private final Economy economy;

    public EconomyBukkitService() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new IllegalStateException("Vault is a hard-dependency!");
        }
        economy = rsp.getProvider();
    }

    public static EconomyBukkitService getInstance() {
        if (singleton == null) {
            singleton = new EconomyBukkitService();
        }

        return singleton;
    }

    /**
     * Gets balance.
     *
     * @param playerUUID the player
     * @return the balance
     */
    public double getBalance(UUID playerUUID) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        return economy.getBalance(player);
    }

    /**
     * Deposit balance.
     *
     * @param playerUUID the player
     * @param amount the amount
     */
    public void depositBalance(UUID playerUUID, double amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        economy.depositPlayer(player, amount);
    }

    /**
     * Withdraw balance.
     *
     * @param playerUUID the player
     * @param amount the amount
     */
    public void withdrawBalance(UUID playerUUID, double amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        economy.withdrawPlayer(player, amount);
    }
}
