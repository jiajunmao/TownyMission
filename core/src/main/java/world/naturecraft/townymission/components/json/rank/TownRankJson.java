/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.rank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.components.entity.SeasonEntry;
import world.naturecraft.townymission.components.entity.SprintEntry;

import java.beans.ConstructorProperties;
import java.util.UUID;

/**
 * The type Season rank.
 */
public class TownRankJson implements Rankable {

    @JsonProperty("townUUID")
    private final UUID townUUID;
    @JsonProperty("points")
    private final int points;

    /**
     * Instantiates a new Season rank.
     *
     * @param townUUID the town id
     * @param points   the points
     */
    @ConstructorProperties({"townUUID", "points"})
    public TownRankJson(UUID townUUID, int points) {
        this.townUUID = townUUID;
        this.points = points;
    }

    /**
     * Instantiates a new Town rank json.
     *
     * @param sprintEntry the sprint entry
     */
    @JsonIgnore
    public TownRankJson(SprintEntry sprintEntry) {
        this.townUUID = sprintEntry.getTownUUID();
        this.points = sprintEntry.getNaturepoints();
    }

    /**
     * Instantiates a new Town rank json.
     *
     * @param seasonEntry the season entry
     */
    @JsonIgnore
    public TownRankJson(SeasonEntry seasonEntry) {
        this.townUUID = seasonEntry.getTownUUID();
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

    @Override
    public String getRankingId() {
        return townUUID.toString();
    }

    /**
     * Gets town id.
     *
     * @return the town id
     */
    @JsonProperty("townId")
    public UUID getTownId() {
        return townUUID;
    }

    @Override
    @JsonIgnore
    public int compareTo(@NotNull Rankable rankable) {
        return points - rankable.getRankingFactor();
    }
}
