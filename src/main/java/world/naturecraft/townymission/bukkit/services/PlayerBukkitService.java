package world.naturecraft.townymission.bukkit.services;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.services.PlayerService;

import java.util.Locale;
import java.util.UUID;

public class PlayerBukkitService extends PlayerService {

    public static PlayerBukkitService singleton;

    public static PlayerBukkitService getInstance() {
        if (singleton == null) {
            singleton = new PlayerBukkitService();
        }

        return singleton;
    }

    @Override
    public int getNumEmptySlot(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return BukkitUtil.getNumEmptySlotsInInventory(player.getInventory());
    }

    @Override
    public void addItem(UUID playerUUID, String material, int amount) {
        Player player = Bukkit.getPlayer(playerUUID);
        Material realMaterial = Material.valueOf(material.toUpperCase(Locale.ROOT));
        ItemStack item = new ItemStack(realMaterial, amount);
        player.getInventory().addItem(item);
    }
}
