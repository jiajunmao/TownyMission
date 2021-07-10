package world.naturecraft.townymission.services;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import world.naturecraft.townymission.utils.BukkitUtil;
import world.naturecraft.townymission.services.PlayerService;

import java.util.Locale;
import java.util.UUID;

/**
 * The type Player bukkit service.
 */
public class PlayerBukkitService extends PlayerService {

    /**
     * The constant singleton.
     */
    public static PlayerBukkitService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
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

    @Override
    public void addMiItem(UUID playerUUID, String category, String id, int amount) {
        Type type = Type.get(category);
        MMOItem mmoItem = MMOItems.plugin.getMMOItem(type, id);
        ItemStack itemStack = mmoItem.newBuilder().build();
        itemStack.setAmount(amount);
        Player player = Bukkit.getPlayer(playerUUID);
        player.getInventory().addItem(itemStack);
    }

    @Override
    public String getPlayerName(UUID playerUUID) {
        return Bukkit.getPlayer(playerUUID).getDisplayName();
    }
}
