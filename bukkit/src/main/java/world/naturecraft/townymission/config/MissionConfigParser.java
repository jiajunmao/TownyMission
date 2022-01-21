/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config;

import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.naturelib.config.NatureConfig;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.json.mission.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
    public static List<MissionJson> parse(MissionType type, NatureConfig fileConfiguration, Town town) {
        List<MissionJson> list = new ArrayList<>();
        int townSize = town.getNumResidents();
        //System.out.println("Town Size: " + townSize);
        //System.out.println("Parsing: " + type.name());
        for (String key : fileConfiguration.getShallowKeys()) {
            int amount = fileConfiguration.getInt(key + ".amount");
            int reward = fileConfiguration.getInt(key + ".reward");
            int hrAllowed = fileConfiguration.getInt(key + ".timeAllowed");
            double actualMultiplier = getActualMultiplier(key, townSize, fileConfiguration);
            amount = (int) Math.ceil(amount * actualMultiplier);
            reward = (int) Math.ceil(reward * actualMultiplier);

            switch (type) {
                case EXPANSION:
                    String world = fileConfiguration.getString(key + ".world");
                    list.add(new ExpansionMissionJson(world, amount, 0, hrAllowed, reward, null));
                    break;
                case MOB:
                    boolean isMm = fileConfiguration.getBoolean(key + ".isMm");
                    String mobType = fileConfiguration.getString(key + ".type");
                    MobMissionJson mobMissionJson = new MobMissionJson(isMm, mobType, amount, 0, hrAllowed, reward, null);
                    list.add(mobMissionJson);
                    break;
                case MONEY:
                    boolean returnable = fileConfiguration.getBoolean(key + ".returnable");
                    list.add(new MoneyMissionJson(amount, 0, hrAllowed, reward, returnable, null));
                    break;
                case RESOURCE:
                    boolean isMi = fileConfiguration.getBoolean(key + ".isMi");
                    String resourceType = fileConfiguration.getString(key + ".type");
                    String miId = fileConfiguration.getString(key + ".miID");
                    returnable = fileConfiguration.getBoolean(key + ".returnable");
                    list.add(new ResourceMissionJson(isMi, resourceType, miId, amount, 0, hrAllowed, reward, returnable, null));
                    break;
                case VOTE:
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
        return parse(missionType, instance.getCustomConfig().getMissionConfig(missionType), null);
    }

    /**
     * Parse list.
     *
     * @param missionType the mission type
     * @param instance the instance
     * @param town the town the player is in
     * @return the list
     */
    public static List<MissionJson> parse(MissionType missionType, TownyMissionBukkit instance, Town town) {
        return parse(missionType, instance.getCustomConfig().getMissionConfig(missionType), town);
    }

    /**
     * Parse all list.
     *
     * @param instance the instance
     * @return the list
     */
    public static List<MissionJson> parseAll(TownyMissionBukkit instance, Town town) {
        List<MissionJson> all = new ArrayList<>();
        for (MissionType missionType : MissionType.values()) {
            if (instance.isMissionEnabled(missionType)) {
                List<MissionJson> customList = parse(missionType, instance, town);
                all.addAll(customList);
            }
        }

        return all;
    }

    /**
     * Get the actual multiplier of the mission based on the town's size.
     *
     * @param key the key indicate config file.
     * @param townSize town's size.
     * @param fileConfiguration file Configuration.
     * @return a double multiplier, default 1.0
     */
    private static double getActualMultiplier(String key, int townSize, NatureConfig fileConfiguration) {

        boolean scale = fileConfiguration.getBoolean(key + ".scale");
        //System.out.println("Scale is: " + scale);
        double multiplier = 1.0;

        if (!scale) {
            return multiplier;
        }

        Collection<String> mulKeys = new HashSet<>(fileConfiguration.getKeys(key + ".scaleMultipliers"));

        if (mulKeys == null || mulKeys.isEmpty()) {
            return multiplier; // default scalar
        }

        for (String stage : mulKeys) {
            int threshold = fileConfiguration.getInt(key + ".scaleMultipliers." + stage + ".size");
            //System.out.println("Current read" + threshold);
            if (threshold <= townSize) {
                multiplier = fileConfiguration.getDouble(key + ".scaleMultipliers." + stage + ".multiplier");
            }
        }

        multiplier = multiplier <= 0.0 ? 1.0 : multiplier; // prevent negative or zero
        //System.out.println("Final mul: " + multiplier);
        return multiplier;
    }
}
