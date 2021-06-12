/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.rank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.RankType;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

/**
 * The type Rank json.
 */
public class RankJson {

    @JsonProperty("rankType")
    private final RankType rankType;
    @JsonProperty("ranks")
    private Map<Integer, TownRankJson> ranks;

    /**
     * Instantiates a new Rank json.
     *
     * @param rankType the rank type
     * @param ranks    the ranks
     */
    @ConstructorProperties({"rankType", "ranks"})
    public RankJson(RankType rankType, Map<Integer, TownRankJson> ranks) {
        this.rankType = rankType;
        this.ranks = ranks;
    }

    /**
     * Construct the SprintRankJson from the sorted RankableList
     * The list MUST BE sorted
     *
     * @param rankType   the rank type
     * @param rankedList the sorted RankableList
     */
    @JsonIgnore
    public RankJson(RankType rankType, List<TownRankJson> rankedList) {
        this.rankType = rankType;
        for (int i = 0; i < rankedList.size(); i++) {
            ranks.put(i, rankedList.get(i));
        }
    }

    /**
     * Parse rank json.
     *
     * @param json the json
     * @return the rank json
     * @throws JsonProcessingException the json processing exception
     */
    public static RankJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, RankJson.class);
    }

    /**
     * Add town.
     *
     * @param rank         the rank
     * @param townRankJson the town rank json
     */
    public void addTown(int rank, TownRankJson townRankJson) {
        ranks.put(rank, townRankJson);
    }

    /**
     * Gets town rank json.
     *
     * @param rank the rank
     * @return the town rank json
     */
    public TownRankJson getTownRankJson(int rank) {
        return ranks.get(rank);
    }

    /**
     * Gets town rank jsons.
     *
     * @return the town rank jsons
     */
    public List<TownRankJson> getTownRankJsons() {
        return List.copyOf(ranks.values());
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
