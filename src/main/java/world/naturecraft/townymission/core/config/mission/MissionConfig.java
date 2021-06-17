/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.config.mission;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.core.components.enums.MissionType;

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
public class MissionConfig {

    private final TownyMissionInstance instance;
    private final Map<MissionType, FileConfiguration> customConfigs;

    /**
     * Instantiates a new Custom config loader.
     *
     * @throws IOException                   the io exception
     * @throws InvalidConfigurationException the invalid configuration exception
     */
    public MissionConfig() throws ConfigLoadingException {
        this.instance = TownyMissionInstance.getInstance();
        this.customConfigs = new HashMap<>();

        try {
            createMissionConfig();
        } catch (IOException | InvalidConfigurationException e) {
            throw new ConfigLoadingException(e);
        }
    }

    private void createMissionConfig() throws IOException, InvalidConfigurationException {
        for (MissionType missionType : MissionType.values()) {
            String fileName = missionType.toString().toLowerCase(Locale.ROOT) + ".yml";
            String filePath = "missions" + File.separator + fileName;
            File customConfig = new File(instance.getInstanceDataFolder(), filePath);
            if (!customConfig.exists()) {
                customConfig.getParentFile().getParentFile().mkdirs();
                customConfig.getParentFile().mkdirs();
                instance.saveInstanceResource(filePath, false);
            }

            FileConfiguration customFileConfig = new YamlConfiguration();
            customFileConfig.load(customConfig);
            MissionConfigValidator.checkMissionConfig(customFileConfig, missionType);
            customConfigs.put(missionType, customFileConfig);
        }
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
