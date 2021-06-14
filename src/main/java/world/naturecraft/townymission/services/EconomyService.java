package world.naturecraft.townymission.services;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import world.naturecraft.townymission.TownyMission;

public class EconomyService {

    private static EconomyService singleton;
    private Economy economy;
    private TownyMission instance;

    public EconomyService(TownyMission instance) {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null)
            throw new IllegalStateException("Vault is a hard-dependency!");

        this.instance = instance;
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new IllegalStateException("Vault is a hard-dependency!");
        }
        economy = rsp.getProvider();
    }

    public static EconomyService getInstance() {
        if (singleton == null) {
            TownyMission instance = (TownyMission) Bukkit.getServer().getPluginManager().getPlugin("TownyMission");
            singleton = new EconomyService(instance);
        }

        return singleton;
    }

    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public void depositBalance(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public void withdrawBalance(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }
}
