package world.naturecraft.townymission.services;

import org.bukkit.inventory.meta.ItemMeta;
import world.naturecraft.naturelib.InstanceType;

public class ItemService {

    private static ItemService singleton;

    public static ItemService getInstance() {
        if (singleton == null) {
            singleton = new ItemService();
        }

        return singleton;
    }

    /**
     * A method to set CustomModelData
     *
     * @param im
     * @param modelData
     */
    public void setCustomModelData(ItemMeta im, int modelData) {
        im.setCustomModelData(modelData);
    }
}
