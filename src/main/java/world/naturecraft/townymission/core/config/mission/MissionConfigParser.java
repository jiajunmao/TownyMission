/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.config.mission;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Custom config parser.
 */
public class MissionConfigParser {

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
                list.add(new ExpansionMissionJson(world, amount, 0, hrAllowed, reward, null));
            } else if (type.equals(MissionType.MOB)) {
                String mobType = section.getString("type");
                MobMissionJson mobMissionJson = new MobMissionJson(EntityType.valueOf(mobType), amount, 0, hrAllowed, reward, null);
                list.add(mobMissionJson);
            } else if (type.equals(MissionType.MONEY)) {
                list.add(new MoneyMissionJson(amount, 0, hrAllowed, reward, null));
            } else if (type.equals(MissionType.RESOURCE)) {
                boolean isMi = section.getBoolean("isMi");
                String resourceType = section.getString("type");
                list.add(new ResourceMissionJson(isMi, Material.valueOf(resourceType), amount, 0, hrAllowed, reward, null));
            } else if (type.equals(MissionType.VOTE)) {
                list.add(new VoteMissionJson(amount, 0, hrAllowed, reward, null));
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
    public static List<MissionJson> parse(MissionType missionType, TownyMissionBukkit instance) {
        return parse(missionType, instance.getCustomConfig().getMissionConfig(missionType));
    }

    /**
     * Parse all list.
     *
     * @param instance the instance
     * @return the list
     */
    public static List<MissionJson> parseAll(TownyMissionBukkit instance) {
        List<MissionJson> all = new ArrayList<>();
        for (MissionType missionType : MissionType.values()) {
            List<MissionJson> customList = parse(missionType, instance);
            all.addAll(customList);
        }

        return all;
    }
}
