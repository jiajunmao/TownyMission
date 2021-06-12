/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.enums.RankType;

import java.beans.ConstructorProperties;

/**
 * The type Season rank.
 */
public class RankJson {

    @JsonProperty("rankType")
    private final RankType rankType;
    @JsonProperty("townId")
    private final String townId;
    @JsonProperty("townName")
    private final String townName;
    @JsonProperty("points")
    private final int points;

    /**
     * Instantiates a new Season rank.
     *
     * @param rankType the rank type
     * @param townId      the town id
     * @param townName    the town name
     * @param points      the points
     */
    @ConstructorProperties({"rankType", "townId", "townName", "points"})
    public RankJson(RankType rankType, String townId, String townName, int points) {
        this.rankType = rankType;
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
    public static RankJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, RankJson.class);
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
