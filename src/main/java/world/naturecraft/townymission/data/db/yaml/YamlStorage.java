/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db.yaml;

import com.Zrips.CMI.Modules.DataBase.DBDAO;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.ConfigLoadingError;
import world.naturecraft.townymission.components.containers.sql.SqlEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.services.TownyMissionService;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The type Yaml storage.
 *
 * @param <T> the type parameter
 */
public abstract class YamlStorage<T> {

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

    /**
     * Instantiates a new Yaml storage.
     *
     * @param instance the instance
     * @param dbType   the db type
     */
    protected YamlStorage(TownyMission instance, DbType dbType) {
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
        String filePath = "data/" + fileName;
        File customConfig = new File(instance.getDataFolder(), filePath);
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
    protected void add(String path, Object content) {
        file.createSection(path);
        file.set(path, String.valueOf(content));
    }

    /**
     * Remove.
     *
     * @param uuid the uuid
     */
    public void remove(String uuid) {
        set(uuid, null);
    }

    /**
     * Set.
     *
     * @param path    the path
     * @param content the content
     */
    protected void set(String path, Object content) {
        file.set(path, content);
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    protected abstract List<T> getEntries();
}
