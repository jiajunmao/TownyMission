/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.config.mission;

import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.*;
import world.naturecraft.townymission.core.config.TownyMissionConfig;

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
    public static List<MissionJson> parse(MissionType type, TownyMissionConfig fileConfiguration) {
        List<MissionJson> list = new ArrayList<>();
        for (String key : fileConfiguration.getShallowKeys()) {
            int amount = fileConfiguration.getInt(key + ".amount");
            int reward = fileConfiguration.getInt(key + ".reward");
            int hrAllowed = fileConfiguration.getInt(key + ".timeAllowed");

            if (type.equals(MissionType.EXPANSION)) {
                String world = fileConfiguration.getString(key + ".world");
                list.add(new ExpansionMissionJson(world, amount, 0, hrAllowed, reward, null));
            } else if (type.equals(MissionType.MOB)) {
                String mobType = fileConfiguration.getString(key + ".type");
                MobMissionJson mobMissionJson = new MobMissionJson((mobType), amount, 0, hrAllowed, reward, null);
                list.add(mobMissionJson);
            } else if (type.equals(MissionType.MONEY)) {
                boolean returnable = fileConfiguration.getBoolean(key + ".returnable");
                list.add(new MoneyMissionJson(amount, 0, hrAllowed, reward, returnable,null));
            } else if (type.equals(MissionType.RESOURCE)) {
                boolean isMi = fileConfiguration.getBoolean(key + ".isMi");
                String resourceType = fileConfiguration.getString(key + ".type");
                boolean returnable = fileConfiguration.getBoolean(key + ".returnable");
                list.add(new ResourceMissionJson(isMi, resourceType, amount, 0, hrAllowed, reward, returnable,null));
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
