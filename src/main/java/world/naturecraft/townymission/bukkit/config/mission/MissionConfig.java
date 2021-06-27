/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.config.mission;

import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.bukkit.config.BukkitConfig;
import world.naturecraft.townymission.bungee.config.BungeeConfig;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.config.TownyMissionConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The type Custom config loader.
 */
public class MissionConfig {

    private final Map<MissionType, TownyMissionConfig> customConfigs;

    /**
     * Instantiates a new Custom config loader.
     *
     * @throws ConfigLoadingException the config loading exception
     */
    public MissionConfig() throws ConfigLoadingException {
        this.customConfigs = new HashMap<>();

        createMissionConfig();
    }

    private void createMissionConfig() {
        for (MissionType missionType : MissionType.values()) {
            String fileName = missionType.toString().toLowerCase(Locale.ROOT) + ".yml";
            String filePath = "missions" + File.separator + fileName;

            TownyMissionConfig tempConfig;
            if (TownyMissionInstanceType.isBukkit()) {
                tempConfig = new BukkitConfig(filePath);
            } else {
                tempConfig = new BungeeConfig(filePath);
            }

            MissionConfigValidator.checkMissionConfig(tempConfig, missionType);
            customConfigs.put(missionType, tempConfig);
        }
    }

    /**
     * Gets mission config.
     *
     * @param missionType the mission type
     * @return the mission config
     */
    public TownyMissionConfig getMissionConfig(MissionType missionType) {
        return customConfigs.getOrDefault(missionType, null);
    }
}
