/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config;

import world.naturecraft.naturelib.config.BukkitConfig;
import world.naturecraft.naturelib.config.NatureConfig;
import world.naturecraft.naturelib.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.enums.MissionType;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The type Custom config loader.
 */
public class MissionConfig {

    private final Map<MissionType, NatureConfig> customConfigs;

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
        TownyMissionBukkit instance = TownyMissionInstance.getInstance();
        for (MissionType missionType : MissionType.values()) {
            String fileName = missionType.toString().toLowerCase(Locale.ROOT) + ".yml";
            String filePath = "missions" + File.separator + fileName;

            NatureConfig tempConfig = new BukkitConfig(filePath);
            tempConfig.updateConfig();
            if (instance.isMissionEnabled(missionType)) {
                MissionConfigValidator.checkMissionConfig(tempConfig, missionType);
                customConfigs.put(missionType, tempConfig);
            }
        }
    }

    /**
     * Gets mission config.
     *
     * @param missionType the mission type
     * @return the mission config
     */
    public NatureConfig getMissionConfig(MissionType missionType) {
        return customConfigs.getOrDefault(missionType, null);
    }
}
