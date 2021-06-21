package world.naturecraft.townymission.core.config;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.config.BukkitConfig;
import world.naturecraft.townymission.bungee.config.BungeeConfig;

/**
 * The type Main config.
 */
public class MainConfig {

    private final TownyMissionConfig config;

    /**
     * Instantiates a new Main config.
     */
    public MainConfig() {
        if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
            config = new BukkitConfig("config.yml");
        } else {
            config = new BungeeConfig("config.yml");
        }
    }

    /**
     * Gets string.
     *
     * @param path the path
     * @return the string
     */
    public String getString(String path) {
        return config.getString(path);
    }

    /**
     * Gets int.
     *
     * @param path the path
     * @return the int
     */
    public Integer getInt(String path) {
        return config.getInt(path);
    }

    /**
     * Gets boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public Boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    /**
     * Set.
     *
     * @param path    the path
     * @param content the content
     */
    public void set(String path, Object content) {
        config.set(path, content);
    }
}
