package world.naturecraft.townymission.core.config;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigSavingException;
import world.naturecraft.townymission.bukkit.config.BukkitConfig;
import world.naturecraft.townymission.bungee.config.BungeeConfig;
import world.naturecraft.townymission.core.components.enums.RankType;

import java.io.File;
import java.io.IOException;

/**
 * The type Stats config.
 */
public class StatsConfig {

    private TownyMissionConfig config;

    /**
     * Instantiates a new Stats config.
     *
     * @throws ConfigLoadingException the config loading exception
     */
    public StatsConfig() throws ConfigLoadingException {
        createConfig();
    }

    /**
     * Gets current.
     *
     * @param rankType the rank type
     * @return the current
     */
    public int getCurrent(RankType rankType) {
        switch (rankType) {
            case SEASON:
                return config.getInt("season.current");
            case SPRINT:
                return config.getInt("sprint.current");
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Gets season started time.
     *
     * @return the season started time
     */
    public long getSeasonStartedTime() {
        return config.getLong("season.startedTime");
    }

    public void createConfig() {
        if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
            config = new BukkitConfig("datastore" + File.separator + "stats.yml");
        } else {
            config = new BungeeConfig("datastore" + File.separator + "stats.yml");
        }
    }

    /**
     * Gets int.
     *
     * @param path the path
     * @return the int
     */
    public int getInt(String path) {
        return config.getInt(path);
    }

    public String getString(String path) {
        return config.getString(path);
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

    /**
     * Gets long.
     *
     * @param path the path
     * @return the long
     */
    public long getLong(String path) {
        return config.getLong(path);
    }

    /**
     * Save.
     */
    public void save() {
        try {
            config.save();
        } catch (IOException e) {
            throw new ConfigSavingException(e);
        }
    }
}
