/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.MissionType;

/**
 * The type Season rank.
 */
public class Rank {

    private final MissionType missionType;
    private final String townId;
    private final String townName;
    private final int points;

    /**
     * Instantiates a new Season rank.
     *
     * @param missionType the mission type
     * @param townId      the town id
     * @param townName    the town name
     * @param points      the points
     */
    public Rank(MissionType missionType, String townId, String townName, int points) {
        this.missionType = missionType;
        this.townId = townId;
        this.townName = townName;
        this.points = points;
    }

    /**
     * Parse rank.
     *
     * @param json the json
     * @return the rank
     * @throws JsonProcessingException the json processing exception
     */
    public static Rank parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Rank.class);
    }

    /**
     * To json string.
     *
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
