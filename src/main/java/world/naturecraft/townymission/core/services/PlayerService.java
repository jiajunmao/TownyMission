package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.services.PlayerBukkitService;
import world.naturecraft.townymission.bungee.services.PlayerBungeeService;

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
            if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
                singleton = PlayerBukkitService.getInstance();
            } else {
                singleton = PlayerBungeeService.getInstance();
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
}
