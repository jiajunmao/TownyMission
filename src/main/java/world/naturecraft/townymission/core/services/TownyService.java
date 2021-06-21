package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.services.TownyBukkitService;
import world.naturecraft.townymission.bungee.services.TownyBungeeService;

import java.util.List;
import java.util.UUID;

/**
 * The type Towny service.
 */
public abstract class TownyService {

    private static TownyService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TownyService getInstance() {
        if (singleton == null) {
            if (TownyMissionInstanceType.isBukkit()) {
                singleton = TownyBukkitService.getInstance();
            } else {
                singleton = TownyBungeeService.getInstance();
            }
        }

        return singleton;
    }

    /**
     * Resident of uuid.
     *
     * @param playerUUID the player uuid
     * @return the uuid
     */
    public abstract UUID residentOf(UUID playerUUID);

    /**
     * Mayor of uuid.
     *
     * @param playerUUID the player uuid
     * @return the uuid
     */
    public abstract UUID mayorOf(UUID playerUUID);

    /**
     * Gets residents.
     *
     * @param townUUID the town uuid
     * @return the residents
     */
    public abstract List<UUID> getResidents(UUID townUUID);
}
