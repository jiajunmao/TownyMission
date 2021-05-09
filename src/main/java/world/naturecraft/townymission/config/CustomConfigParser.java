/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.*;
import world.naturecraft.townymission.components.enums.MissionType;

import java.io.File;
import java.util.*;

public class CustomConfigParser {

    public static List<MissionJson> parse(MissionType type, FileConfiguration fileConfiguration) {
        List<MissionJson> list = new ArrayList<>();
        for (String key : fileConfiguration.getKeys(false)) {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(key);
            int amount = section.getInt("amount");
            int reward = section.getInt("reward");
            int hrAllowed = section.getInt("timeAllowed");

            if (type.equals(MissionType.EXPANSION)) {
                String world = section.getString("world");
                list.add(new Expansion(world, amount, 0, hrAllowed, reward));
            } else if (type.equals(MissionType.MOB)) {
                String mobType = section.getString("type");
                list.add(new Mob(EntityType.valueOf(mobType), amount, 0, hrAllowed, reward));
            } else if (type.equals(MissionType.MONEY)) {
                list.add(new Money(amount, 0, hrAllowed, reward));
            } else if (type.equals(MissionType.RESOURCE)) {
                boolean isMi = section.getBoolean("isMi");
                String resourceType = section.getString("type");
                list.add(new Resource(isMi, Material.valueOf(resourceType), amount, 0, hrAllowed, reward));
            } else if (type.equals(MissionType.VOTE)) {
                list.add(new Vote(amount, 0, hrAllowed, reward));
            }
        }

        return list;
    }

    public static List<MissionJson> parse(MissionType missionType, TownyMission instance) {
        return parse(missionType, instance.getCustomConfig().getMissionConfig(missionType));
    }

    public static List<MissionJson> parseAll(TownyMission instance) {
        List<MissionJson> all = new ArrayList<>();
        for (MissionType missionType : MissionType.values()) {
            FileConfiguration customConfig = instance.getCustomConfig().getMissionConfig(missionType);
            List<MissionJson> customList = parse(missionType, instance);
            all.addAll(customList);
        }

        return all;
    }
}
