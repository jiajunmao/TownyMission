/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.components.json.rank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.core.components.entity.Rankable;
import world.naturecraft.townymission.core.components.entity.SeasonEntry;
import world.naturecraft.townymission.core.components.entity.SprintEntry;

import java.beans.ConstructorProperties;

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
     * @param townId   the town id
     * @param townName the town name
     * @param points   the points
     */
    @ConstructorProperties({"townId", "townName", "points"})
    public TownRankJson(String townId, String townName, int points) {
        this.townId = townId;
        this.townName = townName;
        this.points = points;
    }

    /**
     * Instantiates a new Town rank json.
     *
     * @param sprintEntry the sprint entry
     */
    @JsonIgnore
    public TownRankJson(SprintEntry sprintEntry) {
        this.townId = sprintEntry.getTownUUID();
        this.townName = sprintEntry.getTownName();
        this.points = sprintEntry.getNaturepoints();
    }

    /**
     * Instantiates a new Town rank json.
     *
     * @param seasonEntry the season entry
     */
    @JsonIgnore
    public TownRankJson(SeasonEntry seasonEntry) {
        this.townId = seasonEntry.getTownUUID();
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
    @JsonIgnore
    public static TownRankJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, TownRankJson.class);
    }

    /**
     * To json string.
     *
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
    @JsonIgnore
    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    /**
     * Gets point.
     *
     * @return the point
     */
    @Override
    @JsonProperty("points")
    public int getRankingFactor() {
        return points;
    }

    /**
     * Gets town name.
     *
     * @return the town name
     */
    @JsonProperty("townName")
    public String getTownName() {
        return townName;
    }

    /**
     * Gets town id.
     *
     * @return the town id
     */
    @JsonProperty("townId")
    public String getTownId() {
        return townId;
    }

    @Override
    @JsonIgnore
    public int compareTo(@NotNull Rankable rankable) {
        return points - rankable.getRankingFactor();
    }
}
