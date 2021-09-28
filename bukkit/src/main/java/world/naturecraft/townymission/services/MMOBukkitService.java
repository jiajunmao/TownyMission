package world.naturecraft.townymission.services;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.mmogroup.mmolib.api.item.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The type Mmo bukkit service.
 */
public class MMOBukkitService extends MMOService {

    /**
     * The constant singleton.
     */
    public static MMOBukkitService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MMOBukkitService getInstance() {
        if (singleton == null) {
            singleton = new MMOBukkitService();
        }

        return singleton;
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
    public int getAmountAndSetNull(UUID playerUUID, String type, String id, int amount) {

        int total = 0;
        int index = 0;
        Player player = Bukkit.getPlayer(playerUUID);

        for (ItemStack itemStack : player.getInventory().getContents()) {
            NBTItem nbtItem = NBTItem.get(itemStack);
            if (nbtItem.hasType()
                    && nbtItem.getType().equalsIgnoreCase(type)
                    && nbtItem.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(id)) {

                int tempAmount = itemStack.getAmount();

                if (tempAmount <= amount) {
                    player.getInventory().setItem(index, null);
                    amount -= tempAmount;
                    total += tempAmount;
                } else {
                    // If temp amount is greater than the required amount, only take the required amount
                    int diff = tempAmount - amount;
                    itemStack.setAmount(diff);
                    player.getInventory().setItem(index, null);
                    player.getInventory().setItem(index, itemStack);
                    total += amount;
                    return total;

                }
            }
            index++;
        }

        return total;
    }

    @Override
    public boolean validate(String type, String id) {
        try {
            Type miType = Type.get(type);
            MMOItem item = MMOItems.plugin.getMMOItem(miType, id);
        } catch (NullPointerException e) {
            return false;
        }

        return true;
    }
}
