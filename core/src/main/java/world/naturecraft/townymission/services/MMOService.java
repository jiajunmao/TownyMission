package world.naturecraft.townymission.services;

import org.bukkit.Bukkit;
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
                String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
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

    public abstract int getTotalAndSetNull(UUID playerUUID, String type, String id);

    public abstract boolean validate(String type, String id);
}
