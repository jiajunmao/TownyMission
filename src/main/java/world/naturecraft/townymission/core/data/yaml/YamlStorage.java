/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.yaml;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingError;
import world.naturecraft.townymission.core.components.entity.DataEntity;
import world.naturecraft.townymission.core.components.enums.DbType;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * The type Yaml storage.
 *
 * @param <T> the type parameter
 */
public abstract class YamlStorage<T extends DataEntity> {

    /**
     * The Instance.
     */
    protected TownyMission instance;
    /**
     * The Db type.
     */
    protected DbType dbType;
    /**
     * The File.
     */
    protected FileConfiguration file;

    private File customConfig;

    /**
     * Instantiates a new Yaml storage.
     *
     * @param instance the instance
     * @param dbType   the db type
     */
    public YamlStorage(TownyMission instance, DbType dbType) {
        this.instance = instance;
        this.dbType = dbType;
        createConfig();
    }

    /**
     * Create config.
     *
     * @throws ConfigLoadingError the config loading error
     */
    protected void createConfig() throws ConfigLoadingError {
        String fileName = dbType.toString().toLowerCase(Locale.ROOT) + ".yml";
        String filePath = "datastore" + File.separator + fileName;
        customConfig = new File(instance.getDataFolder(), filePath);
        if (!customConfig.exists()) {
            customConfig.getParentFile().getParentFile().mkdirs();
            customConfig.getParentFile().mkdirs();
            instance.saveResource(filePath, false);
        }

        file = new YamlConfiguration();
        try {
            file.load(customConfig);
        } catch (IOException | InvalidConfigurationException e) {
            throw new ConfigLoadingError(e);
        }
    }

    /**
     * Add.
     *
     * @param path    the path
     * @param content the content
     */
    public void add(String path, Object content) {
        file.createSection(path);
        file.set(path, content);
        try {
            file.save(customConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove.
     *
     * @param uuid the uuid
     */
    public void remove(UUID uuid) {
        set(uuid.toString(), null);
    }

    /**
     * Set.
     *
     * @param path    the path
     * @param content the content
     */
    public void set(String path, Object content) {
        file.set(path, content);
        try {
            file.save(customConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public abstract List<T> getEntries();
}
