/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.components.enums.RewardType;

public abstract class RewardJson implements Rankable {

    @JsonIgnore
    private RewardType rewardType;
    // If this is -1, that means this is others category
    @JsonIgnore
    private int rank;

    public RewardJson(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public static RewardJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, RewardJson.class);
    }

    @JsonIgnore
    public int getRank() {
        return rank;
    }

    @JsonIgnore
    public RewardType getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public void setRank(int rank) {
        this.rank = rank;
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
    public int getRankingFactor() {
        return getRank();
    }

    @Override
    public int compareTo(@NotNull Rankable rankable) {
        return getRank() - rankable.getRankingFactor();
    }

    @JsonIgnore
    public boolean isOthers() {
        return rank == -1;
    }

    public abstract int getAmount();
}
