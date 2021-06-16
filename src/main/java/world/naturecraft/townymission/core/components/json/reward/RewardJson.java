/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.components.json.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.bukkit.api.exceptions.DataProcessException;
import world.naturecraft.townymission.core.components.entity.Rankable;
import world.naturecraft.townymission.core.components.enums.RewardType;
import world.naturecraft.townymission.bukkit.utils.RewardJsonFactory;

/**
 * The type Reward json.
 */
public abstract class RewardJson implements Rankable {

    @JsonIgnore
    private RewardType rewardType;
    // If this is -1, that means this is others category
    @JsonIgnore
    private int rank;

    /**
     * Instantiates a new Reward json.
     *
     * @param rewardType the reward type
     */
    public RewardJson(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    /**
     * Parse reward json.
     *
     * @param json the json
     * @return the reward json
     * @throws JsonProcessingException the json processing exception
     */
    public static RewardJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, RewardJson.class);
    }

    /**
     * Deep copy reward json.
     *
     * @param rewardJson the reward json
     * @return the reward json
     */
    @JsonIgnore
    public static RewardJson deepCopy(RewardJson rewardJson) {
        try {
            String json = rewardJson.toJson();
            return RewardJsonFactory.getJson(json, rewardJson.getRewardType());
        } catch (JsonProcessingException e) {
            throw new DataProcessException(e);
        }
    }

    /**
     * Gets rank.
     *
     * @return the rank
     */
    @JsonIgnore
    public int getRank() {
        return rank;
    }

    /**
     * Sets rank.
     *
     * @param rank the rank
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Gets reward type.
     *
     * @return the reward type
     */
    @JsonIgnore
    public RewardType getRewardType() {
        return rewardType;
    }

    /**
     * Sets reward type.
     *
     * @param rewardType the reward type
     */
    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
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
    @JsonIgnore
    public int getRankingFactor() {
        return getRank();
    }

    @Override
    @JsonIgnore
    public int compareTo(@NotNull Rankable rankable) {
        return getRank() - rankable.getRankingFactor();
    }

    /**
     * Is others boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    public boolean isOthers() {
        return rank == -1;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public abstract int getAmount();

    /**
     * Sets amount.
     *
     * @param amount the amount
     */
    public abstract void setAmount(int amount);

    /**
     * Gets display line.
     *
     * @return the display line
     */
    @JsonIgnore
    public abstract String getDisplayLine();
}
