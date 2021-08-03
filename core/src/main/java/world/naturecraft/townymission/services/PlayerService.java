package world.naturecraft.townymission.services;

import org.bukkit.Bukkit;
import world.naturecraft.naturelib.InstanceType;

import java.util.UUID;

/**
 * The type Player service.
 */
public abstract class PlayerService {

    /**
     * The constant singleton.
     */
    public static PlayerService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static PlayerService getInstance() {
        if (singleton == null) {
            if (InstanceType.isBukkit()) {
                String packageName = PlayerService.class.getPackage().getName();
                String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                try {
                    singleton = (PlayerService) Class.forName(packageName + "." + "PlayerBukkitService").newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return singleton;
    }

    /**
     * Gets num empty slot.
     *
     * @param playerUUID the player uuid
     * @return the num empty slot
     */
    public abstract int getNumEmptySlot(UUID playerUUID);

    /**
     * Add item.
     *
     * @param playerUUID the player uuid
     * @param material   the material
     * @param amount     the amount
     */
    public abstract void addItem(UUID playerUUID, String material, int amount);

    public abstract String getPlayerName(UUID playerUUID);
}
