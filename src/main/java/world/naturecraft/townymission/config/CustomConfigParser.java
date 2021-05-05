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

    public static List<MissionJson> parseExpansion(FileConfiguration expansion) {
        List<MissionJson> list = new ArrayList<>();
        for(String key : expansion.getKeys(false)) {
            System.out.println("Parsing key: " + key);
            ConfigurationSection section = expansion.getConfigurationSection(key);
            System.out.println(section.toString());
            String world = section.getString("world");
            int amount = section.getInt("amount");
            int reward = section.getInt("reward");

            list.add(new Expansion(world, amount, 0, reward));
        }

        return list;
    }

    public static List<MissionJson> parseMob(FileConfiguration expansion) {
        List<MissionJson> list = new ArrayList<>();
        for(String key : expansion.getKeys(false)) {
            ConfigurationSection section = expansion.getConfigurationSection(key);
            String type = section.getString("type");
            int amount = section.getInt("amount");
            int reward = section.getInt("reward");

            list.add(new Mob(EntityType.valueOf(type), amount, 0, reward));
        }

        return list;
    }

    public static List<MissionJson> parseMoney(FileConfiguration expansion) {
        List<MissionJson> list = new ArrayList<>();
        for(String key : expansion.getKeys(false)) {
            ConfigurationSection section = expansion.getConfigurationSection(key);
            int amount = section.getInt("amount");
            int reward = section.getInt("reward");

            list.add(new Money(amount, 0, reward));
        }

        return list;
    }

    public static List<MissionJson> parseResource(FileConfiguration expansion) {
        List<MissionJson> list = new ArrayList<>();
        for(String key : expansion.getKeys(false)) {
            ConfigurationSection section = expansion.getConfigurationSection(key);
            Boolean isMi = section.getBoolean("isMi");
            String type = section.getString("Type");
            int amount = section.getInt("amount");
            int reward = section.getInt("reward");

            list.add(new Resource(isMi, Material.valueOf(type), amount, 0, reward));
        }

        return list;
    }

    public static List<MissionJson> parseVote(FileConfiguration expansion) {
        List<MissionJson> list = new ArrayList<>();
        for(String key : expansion.getKeys(false)) {
            ConfigurationSection section = expansion.getConfigurationSection(key);
            int amount = section.getInt("amount");
            int reward = section.getInt("reward");

            list.add(new Vote(amount, 0, reward));
        }

        return list;
    }

    public static List<MissionJson> parse(MissionType missionType, TownyMission instance) {
        if (missionType.equals(MissionType.MOB)) {
            return parseMob(instance.getCustomConfig().getMissionConfig(MissionType.MOB));
        } else if (missionType.equals(MissionType.MONEY)) {
            return parseMoney(instance.getCustomConfig().getMissionConfig(MissionType.MONEY));
        } else if (missionType.equals(MissionType.EXPANSION)) {
            return parseExpansion(instance.getCustomConfig().getMissionConfig(MissionType.EXPANSION));
        } else if (missionType.equals(MissionType.VOTE)) {
            return parseVote(instance.getCustomConfig().getMissionConfig(MissionType.VOTE));
        } else if (missionType.equals(MissionType.RESOURCE)) {
            return parseResource(instance.getCustomConfig().getMissionConfig(MissionType.RESOURCE));
        } else {
            return null;
        }
    }

    public static Collection<MissionJson> parseAll(TownyMission instance) {
        Collection<MissionJson> all = new HashSet<>();
        for (MissionType missionType : MissionType.values()) {
            FileConfiguration customConfig = instance.getCustomConfig().getMissionConfig(missionType);
            List<MissionJson> customList = parse(missionType, instance);
            all.addAll(customList);
        }

        return all;
    }
}
