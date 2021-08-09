/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config;

import world.naturecraft.naturelib.config.NatureConfig;
import world.naturecraft.naturelib.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.GuiType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.config.mission.MissionConfigValidator;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The type Custom config loader.
 */
public class GuiConfig {

    private final Map<GuiType, NatureConfig> customConfigs;

    /**
     * Instantiates a new Custom config loader.
     *
     * @throws ConfigLoadingException the config loading exception
     */
    public GuiConfig() throws ConfigLoadingException {
        this.customConfigs = new HashMap<>();

        createMissionConfig();
    }

    private void createMissionConfig() {
        for (GuiType guiType : GuiType.values()) {
            TownyMissionBukkit instance = TownyMissionInstance.getInstance();
            String fileName = guiType.toString().toLowerCase(Locale.ROOT) + ".yml";
            String filePath = "missions" + File.separator + fileName;

            NatureConfig tempConfig = new BukkitConfig(filePath);
            customConfigs.put(guiType, tempConfig);
        }
    }

    /**
     * Gets mission config.
     *
     * @param type the gui type
     * @return the mission config
     */
    public NatureConfig getMissionConfig(GuiType type) {
        return customConfigs.getOrDefault(type, null);
    }
}
