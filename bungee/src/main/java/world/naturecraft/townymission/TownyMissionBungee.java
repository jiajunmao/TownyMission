package world.naturecraft.townymission;

import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
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
        mainConfig = new BungeeConfig("config.yml", "bungee/config.yml");
        mainConfig.updateConfig();
        langConfig = new BungeeConfig("lang.yml");
        langConfig.updateConfig();

        // Registering plugin messaging channel
        logger.info("===> Registering Listeners and PMC");
        registerChannel();
        registerListener();
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
        deregisterChannel();
        getProxy().getPluginManager().unregisterCommands(this);
        getProxy().getPluginManager().unregisterListeners(this);
    }

    /**
     * Register channel.
     */
    public void registerChannel() {
        getProxy().registerChannel("townymission:main");
        logger.info("townymission:main PMC channel registered");
    }

    /**
     * Deregister channel.
     */
    public void deregisterChannel() {
        getProxy().unregisterChannel("townymission:main");
        logger.info("townymission:main PMC channel unregistered");
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
    public boolean isMainServer() {
        return false;
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
