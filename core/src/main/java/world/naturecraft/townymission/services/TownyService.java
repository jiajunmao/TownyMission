package world.naturecraft.townymission.services;

import world.naturecraft.naturelib.InstanceType;

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
            if (InstanceType.isBukkit()) {
                String packageName = TownyService.class.getPackage().getName();
                try {
                    singleton = (TownyService) Class.forName(packageName + "." + "TownyBukkitService").newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
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

    public abstract int getNumResidents(UUID townUUID);
}
