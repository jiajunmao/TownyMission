package world.naturecraft.townymission.core.config;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.bukkit.config.BukkitConfig;
import world.naturecraft.townymission.bungee.config.BungeeConfig;

import java.io.IOException;

/**
 * The type Lang config.
 */
public class LangConfig {

    private final TownyMissionInstance instance;
    private TownyMissionConfig config;

    /**
     * Instantiates a new Lang config.
     */
    public LangConfig() {
        this.instance = TownyMissionInstance.getInstance();
        createLanguageConfig();
    }

    private void createLanguageConfig() throws ConfigLoadingException {
        if (TownyMissionInstanceType.isBukkit()) {
            config = new BukkitConfig("lang.yml");
        } else {
            config = new BungeeConfig("lang.yml");
        }
    }

    private void updateLang() throws IOException {
        config.updateConfig("lang.yml");
    }

    /**
     * Gets lang config.
     *
     * @return the lang config
     */
    public TownyMissionConfig getLangConfig() {
        return config;
    }
}
