package world.naturecraft.townymission.core.config;

import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bungee.TownyMissionBungee;
import world.naturecraft.townymission.core.components.enums.ServerType;

/**
 * The type Main config.
 */
public class MainConfig {

    private TownyMissionBukkit bukkitInstance;
    private TownyMissionBungee bungeeInstance;

    /**
     * Instantiates a new Main config.
     */
    public MainConfig() {
        switch (TownyMissionInstanceType.serverType) {
            case BUKKIT:
                bukkitInstance = (TownyMissionBukkit) Bukkit.getPluginManager().getPlugin("TownyMission");
                break;
            case BUNGEE:
                bungeeInstance = (TownyMissionBungee) ProxyServer.getInstance().getPluginManager().getPlugin("TownyMission");
                break;
        }
    }

    /**
     * Gets string.
     *
     * @param path the path
     * @return the string
     */
    public String getString(String path) {
        switch (TownyMissionInstanceType.serverType) {
            case BUKKIT:
                return bukkitInstance.getConfig().getString(path);
            case BUNGEE:
                return bungeeInstance.getConfig().getString(path);
            default:
                return null;
        }
    }

    /**
     * Gets int.
     *
     * @param path the path
     * @return the int
     */
    public Integer getInt(String path) {
        switch (TownyMissionInstanceType.serverType) {
            case BUKKIT:
                return bukkitInstance.getConfig().getInt(path);
            case BUNGEE:
                return bungeeInstance.getConfig().getInt(path);
            default:
                return null;
        }
    }

    /**
     * Gets boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public Boolean getBoolean(String path) {
        switch (TownyMissionInstanceType.serverType) {
            case BUKKIT:
                return bukkitInstance.getConfig().getBoolean(path);
            case BUNGEE:
                return bungeeInstance.getConfig().getBoolean(path);
            default:
                return null;
        }
    }

    /**
     * Set.
     *
     * @param path    the path
     * @param content the content
     */
    public void set(String path, Object content) {
        switch (TownyMissionInstanceType.serverType) {
            case BUKKIT:
                bukkitInstance.getConfig().set(path, content);
                break;
            case BUNGEE:
                bungeeInstance.getConfig().set(path, content);
                break;
        }
    }
}
