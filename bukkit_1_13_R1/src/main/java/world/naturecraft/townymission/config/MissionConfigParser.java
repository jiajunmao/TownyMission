/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config;

import world.naturecraft.naturelib.config.NatureConfig;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.json.mission.*;

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
    public static List<MissionJson> parse(MissionType type, NatureConfig fileConfiguration) {
        List<MissionJson> list = new ArrayList<>();
        //System.out.println("Parsing: " + type.name());
        for (String key : fileConfiguration.getShallowKeys()) {
            int amount = fileConfiguration.getInt(key + ".amount");
            int reward = fileConfiguration.getInt(key + ".reward");
            int hrAllowed = fileConfiguration.getInt(key + ".timeAllowed");

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
            if (instance.isMissionEnabled(missionType)) {
                List<MissionJson> customList = parse(missionType, instance);
                all.addAll(customList);
            }
        }

        return all;
    }
}
