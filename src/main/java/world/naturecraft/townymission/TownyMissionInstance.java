package world.naturecraft.townymission;

import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.core.components.enums.ServerType;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.config.MainConfig;
import world.naturecraft.townymission.core.config.StatsConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * The interface Towny mission instance.
 */
public interface TownyMissionInstance {

    /**
     * Gets instance.
     *
     * @param <T> the type parameter
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    static <T extends TownyMissionInstance> T getInstance() {
        if (TownyMissionInstanceType.serverType.equals(ServerType.BUKKIT)) {
            return (T) Bukkit.getPluginManager().getPlugin("TownyMission");
        } else {
            return (T) ProxyServer.getInstance().getPluginManager().getPlugin("TownyMission");
        }
    }

    /**
     * On enable.
     */
    void onEnable();

    /**
     * On disable.
     */
    void onDisable();

    /**
     * Gets lang entry.
     *
     * @param path the path
     * @return the lang entry
     */
    String getLangEntry(String path);

    /**
     * Gets instance config.
     *
     * @return the instance config
     */
    MainConfig getInstanceConfig();

    /**
     * Gets stats config.
     *
     * @return the stats config
     */
    StatsConfig getStatsConfig();

    /**
     * Gets server type.
     *
     * @return the server type
     */
    ServerType getServerType();

    /**
     * Gets storage type.
     *
     * @return the storage type
     */
    StorageType getStorageType();

    /**
     * Gets instance data folder.
     *
     * @return the instance data folder
     */
    File getInstanceDataFolder();

    /**
     * Gets instance resource.
     *
     * @param filePath the file path
     * @return the instance resource
     */
    InputStream getInstanceResource(String filePath);

    /**
     * Gets logger.
     *
     * @return the logger
     */
    Logger getInstanceLogger();

    /**
     * Save instance resource.
     *
     * @param filePath the file path
     * @param replace  the replace
     * @throws IOException the io exception
     */
    void saveInstanceResource(String filePath, boolean replace) throws IOException;
}
