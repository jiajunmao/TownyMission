/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config.mission;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.manager.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.exceptions.ConfigParsingException;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.config.TownyMissionConfig;
import world.naturecraft.townymission.utils.Util;

/**
 * The type Custom config validator.
 */
public class MissionConfigValidator {

    /**
     * Check mission config.
     *
     * @param missionConfig the mission config
     * @param missionType   the mission type
     */
    public static void checkMissionConfig(TownyMissionConfig missionConfig, MissionType missionType) {
        for (String key : missionConfig.getShallowKeys()) {
            try {
                switch (missionType) {
                    case EXPANSION:
                        String path = key + ".world";
                        String worldStr = missionConfig.getString(path);
                        boolean hasWorld = false;
                        if (Bukkit.getWorld(worldStr) == null) {
                            throwException(missionConfig, key, "World name for entry " + key + " is invalid");
                        }

                        if (!hasWorld) {
                            throwException(missionConfig, key, "No world named: " + worldStr);
                        }
                        // I will assume smart user for now
                        break;
                    case RESOURCE:
                        path = key + ".isMi";
                        String isMi = missionConfig.getString(path);

                        if (!(isMi.equalsIgnoreCase("true") || isMi.equalsIgnoreCase("false"))) {
                            throwException(missionConfig, key, "isMi for entry " + key + " is not boolean value");
                        }

                        path = key + ".type";
                        String type = missionConfig.getString(path);
                        path = key + ".miID";
                        String miID = missionConfig.getString(path);

                        if (isMi.equalsIgnoreCase("true")) {
                            Type miType = Type.get(type);
                            try {
                                MMOItem item = MMOItems.plugin.getMMOItem(miType, miID);
                            } catch (NullPointerException e) {
                                throwException(missionConfig, key, "ResourceType(MI) " + type + " for entry " + key + " is invalid", e);
                            }
                        } else {
                            try {
                                Material.valueOf(type);
                            } catch (IllegalArgumentException e) {
                                throwException(missionConfig, key, "ResourceType " + type + " for entry " + key + " is invalid", e);
                            }
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
                TownyMissionInstance instance = TownyMissionInstance.getInstance();
                instance.getInstanceLogger().warning(e.getMessage());
            }
        }
    }

    private static void throwException(TownyMissionConfig missionConfig, String path, String message, Throwable original) throws ConfigParsingException {
        missionConfig.set(path, null);
        throw new ConfigParsingException(message, original);
    }

    private static void throwException(TownyMissionConfig missionConfig, String path, String message) throws ConfigParsingException {
        missionConfig.set(path, null);
        throw new ConfigParsingException(message);
    }
}