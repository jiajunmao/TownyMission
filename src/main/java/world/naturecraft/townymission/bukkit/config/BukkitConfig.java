package world.naturecraft.townymission.bukkit.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.core.config.TownyMissionConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;

public class BukkitConfig implements TownyMissionConfig {

    private FileConfiguration configuration;
    private File configFile;

    public BukkitConfig(String path) {
        createConfig(path);
    }

    @Override
    public void createConfig(String path) {
        try {
            TownyMissionInstance instance = TownyMissionInstance.getInstance();
            configFile = new File(instance.getInstanceDataFolder(), path);
            if (!configFile.exists()) {
                configFile.getParentFile().getParentFile().mkdirs();
                configFile.getParentFile().mkdirs();
                instance.saveInstanceResource(path, false);
            }

            configuration = new YamlConfiguration();
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new ConfigLoadingException(e);
        }
    }

    @Override
    public void updateConfig(String path) {
        try {
            TownyMissionInstance instance = TownyMissionInstance.getInstance();
            File langFile = new File(instance.getInstanceDataFolder(), path);
            FileConfiguration currLangConfig = new YamlConfiguration();
            currLangConfig.load(langFile);

            FileConfiguration pluginConfig = new YamlConfiguration();
            Reader reader = new InputStreamReader(instance.getInstanceResource(path));
            pluginConfig.load(reader);

            for (String key : pluginConfig.getConfigurationSection("").getKeys(true)) {
                if (currLangConfig.getString(key) == null) {
                    currLangConfig.createSection(key);
                    currLangConfig.set(key, pluginConfig.getString(key));
                }
            }
            currLangConfig.save(langFile);
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
    public void set(String path, Object content) {
        configuration.set(path, content);
    }

    @Override
    public void save() throws IOException {
        configuration.save(configFile);
    }
}
