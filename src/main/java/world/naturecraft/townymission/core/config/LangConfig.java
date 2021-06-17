package world.naturecraft.townymission.core.config;

import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.bungee.TownyMissionBungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class LangConfig {

    private TownyMissionInstance instance;
    private FileConfiguration langConfig;

    public LangConfig() {
        this.instance = TownyMissionInstance.getInstance();
    }

    private void createLanguageConfig() throws ConfigLoadingException {
        try {
            File langFile = new File(instance.getInstanceDataFolder(), "lang.yml");
            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs();
                instance.saveInstanceResource("lang.yml", false);
            } else {
                updateLang();
            }

            langFile = new File(instance.getInstanceDataFolder(), "lang.yml");
            langConfig = new YamlConfiguration();
            langConfig.load(langFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new ConfigLoadingException(e);
        }
    }

    private void updateLang() throws IOException, InvalidConfigurationException {
        File langFile = new File(instance.getInstanceDataFolder(), "lang.yml");
        FileConfiguration currLangConfig = new YamlConfiguration();
        currLangConfig.load(langFile);

        FileConfiguration pluginConfig = new YamlConfiguration();
        Reader reader = new InputStreamReader(instance.getInstanceResource("lang.yml"));
        pluginConfig.load(reader);

        for (String key : pluginConfig.getConfigurationSection("").getKeys(true)) {
            if (currLangConfig.getString(key) == null) {
                currLangConfig.createSection(key);
                currLangConfig.set(key, pluginConfig.getString(key));
            }
        }
        currLangConfig.save(langFile);
    }

    /**
     * Gets lang config.
     *
     * @return the lang config
     */
    public FileConfiguration getLangConfig() {
        return langConfig;
    }
}
