package world.naturecraft.townymission.core.services;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.core.components.enums.ServerType;

public class EconomyService extends TownyMissionService {

    private static EconomyService singleton;
    private Economy economy;

    public EconomyService(TownyMissionInstance instance) {
        super(instance);
        if (Bukkit.getPluginManager().getPlugin("Vault") == null)
            throw new IllegalStateException("Vault is a hard-dependency!");

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new IllegalStateException("Vault is a hard-dependency!");
        }
        economy = rsp.getProvider();
    }

    public static EconomyService getInstance() {
        if (singleton == null) {
            singleton = new EconomyService(TownyMissionInstance.getInstance());
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
