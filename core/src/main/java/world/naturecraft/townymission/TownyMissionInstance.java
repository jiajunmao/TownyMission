package world.naturecraft.townymission;

import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.config.TownyMissionConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * The interface Towny mission instance.
 */
public interface TownyMissionInstance {

    static <T extends TownyMissionInstance> T getInstance() {
        return TownyMissionInstanceType.getInstance();
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
    TownyMissionConfig getInstanceConfig();

    /**
     * Gets stats config.
     *
     * @return the stats config
     */
    TownyMissionConfig getStatsConfig();

    /**
     * Gets storage type.
     *
     * @return the storage type
     */
    StorageType getStorageType();

    boolean isMainServer();

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
