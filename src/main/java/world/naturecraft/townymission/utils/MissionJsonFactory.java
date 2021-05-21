/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.utils;

import world.naturecraft.townymission.components.containers.json.*;
import world.naturecraft.townymission.components.enums.MissionType;

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
                return VoteJson.class;
            case "MONEY":
                return MoneyJson.class;
            case "EXPANSION":
                return ExpansionJson.class;
            case "MOB":
                return MobJson.class;
            case "RESOURCE":
                return ResourceJson.class;
            default:
                return null;
        }
    }
}
