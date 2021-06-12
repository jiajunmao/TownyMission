/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.containers.entity.Rankable;
import world.naturecraft.townymission.components.containers.entity.SeasonEntry;
import world.naturecraft.townymission.components.containers.entity.SprintEntry;
import world.naturecraft.townymission.components.enums.RankType;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;

/**
 * The type Season rank.
 */
public class TownRankJson implements Rankable {

    @JsonProperty("townId")
    private final String townId;
    @JsonProperty("townName")
    private final String townName;
    @JsonProperty("points")
    private final int points;

    /**
     * Instantiates a new Season rank.
     *
     * @param townId      the town id
     * @param townName    the town name
     * @param points      the points
     */
    @ConstructorProperties({"townId", "townName", "points"})
    public TownRankJson(String townId, String townName, int points) {
        this.townId = townId;
        this.townName = townName;
        this.points = points;
    }

    public TownRankJson(SprintEntry sprintEntry) {
        this.townId = sprintEntry.getTownID();
        this.townName = sprintEntry.getTownName();
        this.points = sprintEntry.getNaturepoints();
    }

    public TownRankJson(SeasonEntry seasonEntry) {
        this.townId = seasonEntry.getTownID();
        this.townName = seasonEntry.getTownName();
        this.points = seasonEntry.getSeasonPoint();
    }

    /**
     * Parse rank.
     *
     * @param json the json
     * @return the rank
     * @throws JsonProcessingException the json processing exception
     */
    public static TownRankJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, TownRankJson.class);
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

    /**
     * Gets point.
     *
     * @return the point
     */
    @Override
    public int getPoint() {
        return points;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Override
    public String getID() {
        return townId;
    }

    @Override
    public int compareTo(@NotNull Rankable rankable) {
        return points - rankable.getPoint();
    }
}
