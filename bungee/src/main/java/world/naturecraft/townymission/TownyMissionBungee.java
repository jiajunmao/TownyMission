package world.naturecraft.townymission;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.bstats.bungeecord.Metrics;
import world.naturecraft.naturelib.InstanceType;
import world.naturecraft.naturelib.components.enums.ServerType;
import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.naturelib.config.BungeeConfig;
import world.naturecraft.naturelib.config.NatureConfig;
import world.naturecraft.townymission.components.enums.LogLevel;
import world.naturecraft.townymission.listener.PMCListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * The type Towny mission bungee.
 */
public class TownyMissionBungee extends Plugin implements TownyMissionInstance {

    private final Logger logger = this.getLogger();
    private Configuration config;
    private NatureConfig mainConfig;
    private NatureConfig langConfig;

    @Override
    public void onEnable() {
        logger.info("===> Enabling TownyMission");
        InstanceType.serverType = ServerType.BUNGEE;
        InstanceType.registerInstance(this);
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

        // bStats
        int pluginId = 1234; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
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
    public String getLangEntry(String s, boolean b) {
        if (b) {
            return langConfig.getString("prefix") + " " + langConfig.getString(s);
        } else {
            return langConfig.getString(s);
        }
    }

    @Override
    public NatureConfig getInstanceConfig() {
        return mainConfig;
    }

    @Override
    public NatureConfig getStatsConfig() {
        return null;
    }

    @Override
    public List<String> getGuiLangEntries(String path) {
        return null;
    }

    @Override
    public String getGuiLangEntry(String path) {
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

    @Override
    public void reloadConfigs() {
    }

    @Override
    public int debugLevel() {
        return getInstanceConfig().getInt("verbose");
    }

    @Override
    public void log(String trace) {
        log(trace, LogLevel.INFO);
    }

    @Override
    public void log(String trace, LogLevel level) {
        switch (level) {
            case INFO:
                if (debugLevel() == 2)
                    logger.info(trace);
                break;
            case WARNING:
                if (debugLevel() >= 1)
                    logger.warning(trace);
                break;
            case ERROR:
                if (debugLevel() >= 1)
                    logger.severe(trace);
                break;
        }
    }
}
