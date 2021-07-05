package world.naturecraft.townymission.config;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.api.exceptions.ConfigSavingException;
import world.naturecraft.townymission.TownyMissionBungee;
import world.naturecraft.townymission.config.TownyMissionConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;

/**
 * The type Bungee config.
 */
public class BungeeConfig implements TownyMissionConfig {

    private Configuration configuration;
    private File configFile;
    private String pluginPath;
    private String folderPath;

    /**
     * Instantiates a new Bungee config.
     *
     * @param path the path
     */
    public BungeeConfig(String path) {
        this.pluginPath = path;
        this.folderPath = path;
        createConfig(path);
    }

    public BungeeConfig(String pluginPath, String folderPath) {
        this.pluginPath = pluginPath;
        this.folderPath = folderPath;
        createConfig(pluginPath);
    }
    @Override
    public void createConfig(String path) {
        try {
            TownyMissionBungee instance = TownyMissionInstance.getInstance();
            configFile = new File(instance.getInstanceDataFolder(), folderPath);
            if (!configFile.exists()) {
                try (InputStream in = instance.getResourceAsStream(pluginPath)) {
                    Files.copy(in, configFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(instance.getInstanceDataFolder(), folderPath));
        } catch (IOException e) {
            throw new ConfigLoadingException(e);
        }
    }

    @Override
    public void updateConfig(String path) {
        try {
            TownyMissionBungee instance = TownyMissionInstance.getInstance();

            Configuration currLangConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(instance.getInstanceDataFolder(), path));
            Configuration pluginConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(instance.getResourceAsStream(path));

            for (String key : pluginConfig.getKeys()) {
                if (currLangConfig.getString(key) == null) {
                    currLangConfig.set(key, pluginConfig.getString(key));
                }
            }

            ConfigurationProvider.getProvider(YamlConfiguration.class).save(currLangConfig, configFile);
        } catch (IOException e) {
            throw new ConfigLoadingException(e);
        }
    }

    public int getInt(String path) {
        return configuration.getInt(path);
    }

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
        return configuration.getKeys();
    }

    @Override
    public Collection<String> getKeys(String path) {
        return configuration.getSection(path).getKeys();
    }

    @Override
    public Collection<String> getStringList(String path) {
        return configuration.getStringList(path);
    }

    public void set(String path, Object content) {
        configuration.set(path, content);
    }

    public void save() throws ConfigSavingException {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
        } catch (IOException e) {
            throw new ConfigSavingException(e);
        }
    }

}
