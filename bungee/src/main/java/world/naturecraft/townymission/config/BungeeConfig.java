package world.naturecraft.townymission.config;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import world.naturecraft.naturelib.config.NatureConfig;
import world.naturecraft.naturelib.exceptions.ConfigLoadingException;
import world.naturecraft.naturelib.exceptions.ConfigSavingException;
import world.naturecraft.townymission.TownyMissionBungee;
import world.naturecraft.townymission.TownyMissionInstance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The type Bungee config.
 */
public class BungeeConfig implements NatureConfig {

    private Configuration configuration;
    private File configFile;
    private String targetPath;
    private String sourcePath;

    /**
     * Instantiates a new Bungee config.
     *
     * @param path the path
     */
    public BungeeConfig(String path) {
        this.targetPath = path;
        this.sourcePath = path;
        createConfig();
    }

    public BungeeConfig(String targetPath, String sourcePath) {
        this.targetPath = targetPath;
        this.sourcePath = sourcePath;
        createConfig();
    }

    @Override
    public void createConfig() {
        try {
            TownyMissionBungee instance = TownyMissionInstance.getInstance();
            configFile = new File(instance.getInstanceDataFolder(), targetPath);
            if (!configFile.exists()) {
                try (InputStream in = instance.getResourceAsStream(sourcePath)) {
                    Files.copy(in, configFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(instance.getInstanceDataFolder(), targetPath));
        } catch (IOException e) {
            throw new ConfigLoadingException(e);
        }
    }

    @Override
    public void updateConfig() {
        try {
            System.out.println("Updating " + targetPath);
            TownyMissionBungee instance = TownyMissionInstance.getInstance();

            Configuration currLangConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(instance.getInstanceDataFolder(), targetPath));
            Configuration pluginConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(instance.getResourceAsStream(sourcePath));

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
    public Collection<String> getDeepKeys(String path) {
        ArrayList<String> list = new ArrayList<>();
        Collection<String> keys = configuration.getSection(path).getKeys();
        if (!keys.isEmpty()) {
            for (String key : keys) {
                list.addAll(getDeepKeys(path + "." + key));
            }
        } else {
            list.add(path);
        }
        return list;
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
