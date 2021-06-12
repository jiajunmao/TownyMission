/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.RankType;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RankJson {

    @JsonProperty("rankType")
    private RankType rankType;
    @JsonProperty("ranks")
    private Map<Integer, TownRankJson> ranks;

    @ConstructorProperties({"rankType", "ranks"})
    public RankJson(RankType rankType, Map<Integer, TownRankJson> ranks) {
        this.rankType = rankType;
        this.ranks = ranks;
    }

    /**
     * Construct the SprintRankJson from the sorted RankableList
     * The list MUST BE sorted
     *
     * @param rankedList the sorted RankableList
     */
    @JsonIgnore
    public RankJson(RankType rankType, List<TownRankJson> rankedList) {
        this.rankType = rankType;
        for(int i = 0; i < rankedList.size(); i++) {
            ranks.put(i, rankedList.get(i));
        }
    }

    public void addTown(int rank, TownRankJson townRankJson) {
        ranks.put(rank, townRankJson);
    }

    public TownRankJson getTownRankJson(int rank) {
        return ranks.get(rank);
    }

    public List<TownRankJson> getTownRankJsons() {
        return List.copyOf(ranks.values());
    }

    public static RankJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, RankJson.class);
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
