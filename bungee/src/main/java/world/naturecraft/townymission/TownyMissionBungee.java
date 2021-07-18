package world.naturecraft.townymission;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import world.naturecraft.townymission.components.enums.ServerType;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.config.BungeeConfig;
import world.naturecraft.townymission.config.TownyMissionConfig;
import world.naturecraft.townymission.listener.PMCListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * The type Towny mission bungee.
 */
public class TownyMissionBungee extends Plugin implements TownyMissionInstance {

    private final Logger logger = this.getLogger();
    private Configuration config;
    private TownyMissionConfig mainConfig;
    private TownyMissionConfig langConfig;

    @Override
    public void onEnable() {
        logger.info("===> Enabling TownyMission");
        TownyMissionInstanceType.serverType = ServerType.BUNGEE;
        // This is loading in the config file
        logger.info("===> Registering and parsing configs");
        saveDefaultConfig();
        mainConfig = new BungeeConfig("bungee/config.yml", "config.yml");
        langConfig = new BungeeConfig("lang.yml");
        langConfig.updateConfig("lang.yml");

        // Registering plugin messaging channel
        logger.info("===> Registering Listeners and PMC");
        registerChannel();
        registerListener();
    }

    /**
     * Save default config.
     */
    public void saveDefaultConfig() {
        try {
            createPlugin();
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            onDisable();
        }
    }

    /**
     * Register listener.
     */
    public void registerListener() {
        getProxy().getPluginManager().registerListener(this, new PMCListener());
    }

    @Override
    public void onDisable() {
        logger.info("===> Disabling TownyMission");
        getProxy().unregisterChannel("townymission:main");
        getProxy().getPluginManager().unregisterCommands(this);
        getProxy().getPluginManager().unregisterListeners(this);
    }

    /**
     * Create plugin.
     *
     * @throws IOException the io exception
     */
    public void createPlugin() throws IOException {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            InputStream in = getResourceAsStream("world/naturecraft/townymission/bungee/src/main/resources/bungee/config.yml");
            Files.copy(in, file.toPath());
        }
    }

    /**
     * Register channel.
     */
    public void registerChannel() {
        TownyMissionBungee townyMissionBungee = TownyMissionInstance.getInstance();
        townyMissionBungee.getProxy().registerChannel("townymission:main");
        townyMissionBungee.logger.info("townymission:main PMC channel registered");
    }

    /**
     * Deregister channel.
     */
    public void deregisterChannel() {
        TownyMissionBungee townyMissionBungee = TownyMissionInstance.getInstance();
        townyMissionBungee.getProxy().unregisterChannel("townymission:main");
        townyMissionBungee.logger.info("townymission:main PMC channel unregistered");
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public Configuration getConfig() {
        return config;
    }

    @Override
    public String getLangEntry(String path) {
        return langConfig.getString(path);
    }

    @Override
    public TownyMissionConfig getInstanceConfig() {
        return mainConfig;
    }

    @Override
    public TownyMissionConfig getStatsConfig() {
        return null;
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.valueOf(mainConfig.getString("storage").toUpperCase(Locale.ROOT));
    }

    @Override
    public File getInstanceDataFolder() {
        return this.getDataFolder();
    }

    @Override
    public InputStream getInstanceResource(String filePath) {
        return getResourceAsStream(filePath);
    }

    /**
     * Gets logger.
     *
     * @return the logger
     */
    @Override
    public Logger getInstanceLogger() {
        return this.getLogger();
    }

    @Override
    public void saveInstanceResource(String filePath, boolean replace) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || replace) {
            InputStream in = getResourceAsStream(filePath);
            Files.copy(in, file.toPath());
        }
    }
}
