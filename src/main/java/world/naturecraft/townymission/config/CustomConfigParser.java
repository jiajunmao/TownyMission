/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.*;
import world.naturecraft.townymission.components.enums.MissionType;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Custom config parser.
 */
public class CustomConfigParser {

    /**
     * Parse list.
     *
     * @param type              the type
     * @param fileConfiguration the file configuration
     * @return the list
     */
    public static List<MissionJson> parse(MissionType type, FileConfiguration fileConfiguration) {
        List<MissionJson> list = new ArrayList<>();
        for (String key : fileConfiguration.getKeys(false)) {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(key);
            int amount = section.getInt("amount");
            int reward = section.getInt("reward");
            int hrAllowed = section.getInt("timeAllowed");

            if (type.equals(MissionType.EXPANSION)) {
                String world = section.getString("world");
                list.add(new ExpansionJson(world, amount, 0, hrAllowed, reward, null));
            } else if (type.equals(MissionType.MOB)) {
                String mobType = section.getString("type");
                MobJson mobJson = new MobJson(EntityType.valueOf(mobType), amount, 0, hrAllowed, reward, null);
                list.add(mobJson);
            } else if (type.equals(MissionType.MONEY)) {
                list.add(new MoneyJson(amount, 0, hrAllowed, reward, null));
            } else if (type.equals(MissionType.RESOURCE)) {
                boolean isMi = section.getBoolean("isMi");
                String resourceType = section.getString("type");
                list.add(new ResourceJson(isMi, Material.valueOf(resourceType), amount, 0, hrAllowed, reward, null));
            } else if (type.equals(MissionType.VOTE)) {
                list.add(new VoteJson(amount, 0, hrAllowed, reward, null));
            }
        }

        return list;
    }

    /**
     * Parse list.
     *
     * @param missionType the mission type
     * @param instance    the instance
     * @return the list
     */
    public static List<MissionJson> parse(MissionType missionType, TownyMission instance) {
        return parse(missionType, instance.getCustomConfig().getMissionConfig(missionType));
    }

    /**
     * Parse all list.
     *
     * @param instance the instance
     * @return the list
     */
    public static List<MissionJson> parseAll(TownyMission instance) {
        List<MissionJson> all = new ArrayList<>();
        for (MissionType missionType : MissionType.values()) {
            List<MissionJson> customList = parse(missionType, instance);
            all.addAll(customList);
        }

        return all;
    }
}
