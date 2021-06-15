/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config.mission;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.ConfigParsingException;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.Util;

import javax.naming.ConfigurationException;
import java.text.ParseException;

/**
 * The type Custom config validator.
 */
public class MissionConfigValidator {


    public static void checkMissionConfig(FileConfiguration missionConfig, MissionType missionType) {
        for (String key : missionConfig.getKeys(false)) {
            try {
                switch (missionType) {
                    case EXPANSION:
                        String path = key + ".world";
                        String worldStr = missionConfig.getString(path);
                        boolean hasWorld = false;
                        for (World world : Bukkit.getServer().getWorlds()) {
                            if (world.getName().equalsIgnoreCase(worldStr)) {
                                hasWorld = true;
                            }
                        }

                        if (!hasWorld) {
                            throwException(missionConfig, key, "No world named: " + worldStr);
                        }
                        break;
                    case RESOURCE:
                        path = key + ".isMi";
                        String isMi = missionConfig.getString(path);

                        if (!(isMi.equalsIgnoreCase("true") || isMi.equalsIgnoreCase("false"))) {
                            throwException(missionConfig, key, "isMi for entry " + key + " is not boolean value");
                        }

                        path = key + ".type";
                        String type = missionConfig.getString(path);

                        try {
                            Material.valueOf(type);
                        } catch (IllegalArgumentException e) {
                            throwException(missionConfig, key, "ResourceType " + type + " for entry " + key + " is invalid", e);
                        }
                        break;
                    case MOB:
                        path = key + ".type";
                        String mobType = missionConfig.getString(path);

                        try {
                            EntityType.valueOf(mobType);
                        } catch (IllegalArgumentException e) {
                            throwException(missionConfig, key, "MobType " + mobType + " for entry " + key + " is invalid", e);
                        }
                        break;
                }

                String path = key + ".amount";
                String content = missionConfig.getString(path);
                if (!Util.isInt(content)) {
                    throwException(missionConfig, key, "Amount of entry " + key + " is not a integer");
                }

                path = key + ".reward";
                content = missionConfig.getString(path);
                if (!Util.isInt(content)) {
                    throwException(missionConfig, key, "Reward of entry " + key + " is not a integer");
                }

                path = key + ".timeAllowed";
                content = missionConfig.getString(path);
                if (!Util.isInt(content)) {
                    throwException(missionConfig, key, "TimeAllowed of entry " + key + " is not a integer");
                }
            } catch (ConfigParsingException e) {
                TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
                instance.getLogger().warning(e.getMessage());
            }
        }
    }

    private static void throwException(FileConfiguration missionConfig, String path, String message, Throwable original) throws ConfigParsingException {
        missionConfig.set(path, null);
        throw new ConfigParsingException(message, original);
    }

    private static void throwException(FileConfiguration missionConfig, String path, String message) throws ConfigParsingException {
        missionConfig.set(path, null);
        throw new ConfigParsingException(message);
    }
}