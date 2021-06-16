/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.*;

/**
 * The type Mission json factory.
 */
public class MissionJsonFactory {
    /**
     * To class class.
     *
     * @param type the type
     * @return the class
     */
    public static Class<? extends MissionJson> toClass(MissionType type) {
        switch (type.name()) {
            case "VOTE":
                return VoteMissionJson.class;
            case "MONEY":
                return MoneyMissionJson.class;
            case "EXPANSION":
                return ExpansionMissionJson.class;
            case "MOB":
                return MobMissionJson.class;
            case "RESOURCE":
                return ResourceMissionJson.class;
            default:
                return null;
        }
    }

    /**
     * Gets json.
     *
     * @param missionJson the mission json
     * @param missionType the mission type
     * @return the json
     * @throws JsonProcessingException the json processing exception
     */
    public static MissionJson getJson(String missionJson, MissionType missionType) throws JsonProcessingException {
        switch (missionType.name()) {
            case "VOTE":
                return (VoteMissionJson.parse(missionJson));
            case "MONEY":
                return (MoneyMissionJson.parse(missionJson));
            case "MOB":
                return (MobMissionJson.parse(missionJson));
            case "EXPANSION":
                return (ExpansionMissionJson.parse(missionJson));
            case "RESOURCE":
                return (ResourceMissionJson.parse(missionJson));
            default:
                return null;
        }
    }
}
