package world.naturecraft.townymission.config;

import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.api.exceptions.ConfigSavingException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The type Bukkit config.
 */
public class BukkitConfig implements TownyMissionConfig {

    private FileConfiguration configuration;
    private File configFile;
    private String sourcePath;
    private String targetPath;

    /**
     * Instantiates a new Bukkit config.
     *
     * @param path the path
     */
    public BukkitConfig(String path) {
        this.sourcePath = path;
        this.targetPath = path;
        createConfig();
    }

    public BukkitConfig(String sourcePath, String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        createConfig();
    }

    @Override
    public void createConfig() {
        try {
            TownyMissionInstance instance = TownyMissionInstance.getInstance();
            configFile = new File(instance.getInstanceDataFolder(), targetPath);
            if (!configFile.exists()) {
                configFile.getParentFile().getParentFile().mkdirs();
                configFile.getParentFile().mkdirs();
                instance.saveInstanceResource(sourcePath, false);
            }

            configuration = new YamlConfiguration();
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new ConfigLoadingException(e);
        }
    }

    @Override
    public void updateConfig() {
        try {
            TownyMissionBukkit instance = TownyMissionInstance.getInstance();

            File currFile = new File(instance.getInstanceDataFolder(), targetPath);
            FileConfiguration currConfig = new YamlConfiguration();
            currConfig.load(currFile);

            System.out.println("Updating " + targetPath);
            ConfigUpdater.update(instance, sourcePath, currFile, new ArrayList<>());

            instance.reloadConfigs();
        } catch (IOException | InvalidConfigurationException e) {
            throw new ConfigLoadingException(e);
        }
    }

    @Override
    public int getInt(String path) {
        return configuration.getInt(path);
    }

    @Override
    public String getString(String path) {
        return configuration.getString(path);
    }

    @Override
    public double getDouble(String path) {
        return configuration.getDouble(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    @Override
    public long getLong(String path) {
        return configuration.getLong(path);
    }

    @Override
    public Collection<String> getShallowKeys() {
        return configuration.getKeys(false);
    }

    @Override
    public Collection<String> getKeys(String path) {
        ConfigurationSection section = configuration.getConfigurationSection(path);
        if (section == null)
            return null;
        return section.getKeys(false);
    }

    @Override
    public Collection<String> getDeepKeys(String path) {
        return configuration.getConfigurationSection(path).getKeys(true);
    }

    @Override
    public Collection<String> getStringList(String path) {
        return configuration.getStringList(path);
    }

    @Override
    public void set(String path, Object content) {
        configuration.set(path, content);
    }

    @Override
    public void save() throws ConfigSavingException {
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            throw new ConfigSavingException(e);
        }
    }
}
