package world.naturecraft.townymission.core.config;

import world.naturecraft.townymission.bukkit.api.exceptions.ConfigSavingException;

import java.io.IOException;
import java.util.Collection;

/**
 * The interface Towny mission config.
 */
public interface TownyMissionConfig {

    /**
     * Create config.
     *
     * @param path the path
     */
    void createConfig(String path);

    /**
     * Update config.
     *
     * @param path the path
     */
    void updateConfig(String path);

    /**
     * Gets int.
     *
     * @param path the path
     * @return the int
     */
    int getInt(String path);

    /**
     * Gets string.
     *
     * @param path the path
     * @return the string
     */
    String getString(String path);

    /**
     * Gets double.
     *
     * @param path the path
     * @return the double
     */
    double getDouble(String path);

    /**
     * Gets boolean.
     *
     * @param path the path
     * @return the boolean
     */
    boolean getBoolean(String path);

    /**
     * Gets long.
     *
     * @param path the path
     * @return the long
     */
    long getLong(String path);

    /**
     * Gets shallow keys.
     *
     * @return the shallow keys
     */
    Collection<String> getShallowKeys();

    Collection<String> getKeys(String path);

    /**
     * Set.
     *
     * @param path    the path
     * @param content the content
     */
    void set(String path, Object content);

    /**
     * Save.
     *
     * @throws ConfigSavingException the io exception
     */
    void save() throws ConfigSavingException;
}
