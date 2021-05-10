/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.MissionType;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The type Custom config loader.
 */
public class CustomConfigLoader {

    private final TownyMission instance;
    private final Map<MissionType, FileConfiguration> customConfigs;
    private FileConfiguration langConfig;

    /**
     * Instantiates a new Custom config loader.
     *
     * @param instance the instance
     * @throws IOException                   the io exception
     * @throws InvalidConfigurationException the invalid configuration exception
     */
    public CustomConfigLoader(TownyMission instance) throws IOException, InvalidConfigurationException {
        this.instance = instance;
        this.customConfigs = new HashMap<>();

        createLanguageConfig();
        createMissionConfig();
    }

    private void createMissionConfig() throws IOException, InvalidConfigurationException {
        for (MissionType missionType : MissionType.values()) {
            String fileName = missionType.toString().toLowerCase(Locale.ROOT) + ".yml";
            String filePath = "missions/" + fileName;
            File customConfig = new File(instance.getDataFolder(), filePath);
            if (!customConfig.exists()) {
                customConfig.getParentFile().getParentFile().mkdirs();
                customConfig.getParentFile().mkdirs();
                instance.saveResource(filePath, false);
            }

            FileConfiguration customFileConfig = new YamlConfiguration();
            customFileConfig.load(customConfig);
            customConfigs.put(missionType, customFileConfig);
        }
    }

    private void createLanguageConfig() throws IOException, InvalidConfigurationException {
        File langFile = new File(instance.getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            instance.saveResource("lang.yml", false);
        } else {
            updateLang();

        }

        langFile = new File(instance.getDataFolder(), "lang.yml");
        langConfig = new YamlConfiguration();
        langConfig.load(langFile);
    }

    private void updateLang() throws IOException, InvalidConfigurationException {
        File langFile = new File(instance.getDataFolder(), "lang.yml");
        FileConfiguration currLangConfig = new YamlConfiguration();
        currLangConfig.load(langFile);

        FileConfiguration pluginConfig = new YamlConfiguration();
        Reader reader = new InputStreamReader(instance.getResource("lang.yml"));
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

    /**
     * Gets mission config.
     *
     * @param missionType the mission type
     * @return the mission config
     */
    public FileConfiguration getMissionConfig(MissionType missionType) {
        return customConfigs.getOrDefault(missionType, null);
    }
}
