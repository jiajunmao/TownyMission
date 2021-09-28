package world.naturecraft.townymission.services;

import world.naturecraft.naturelib.InstanceType;

import java.util.UUID;

public abstract class MMOService {

    /**
     * The constant singleton.
     */
    public static MMOService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MMOService getInstance() {
        if (singleton == null) {
            if (InstanceType.isBukkit()) {
                String packageName = MMOService.class.getPackage().getName();
                try {
                    singleton = (MMOService) Class.forName(packageName + "." + "MMOBukkitService").newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return singleton;
    }

    public abstract void addMiItem(UUID playerUUID, String category, String id, int amount);

    public abstract int getAmountAndSetNull(UUID playerUUID, String type, String id, int amount);

    public abstract boolean validate(String type, String id);
}
