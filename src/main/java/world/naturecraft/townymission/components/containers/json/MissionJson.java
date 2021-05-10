/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.MissionType;

/**
 * The type Json entry.
 */
public abstract class MissionJson {

    @JsonIgnore
    private final MissionType missionType;
    @JsonProperty("reward")
    private final int reward;
    @JsonProperty("hrAllowed")
    private final int hrAllowed;
    @JsonProperty("amount")
    private final int amount;
    @JsonProperty("completed")
    private int completed;

    /**
     * Instantiates a new Mission json.
     *
     * @param missionType the mission type
     * @param amount      the amount
     * @param completed   the completed
     * @param hrAllowed   the hr allowed
     * @param reward      the reward
     */
    protected MissionJson(MissionType missionType, int amount, int completed, int hrAllowed, int reward) {
        this.missionType = missionType;
        this.reward = reward;
        this.amount = amount;
        this.completed = completed;
        this.hrAllowed = hrAllowed;
    }

    public String toString() {
        try {
            return toJson();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
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
     * Get the formatted display line when needed to be displayed in MC chat
     *
     * @return the formatted line
     */
    @JsonIgnore
    public abstract String getDisplayLine();

    /**
     * Gets reward.
     *
     * @return the reward
     */
    public int getReward() {
        return reward;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Gets completed.
     *
     * @return the completed
     */
    public int getCompleted() {
        return completed;
    }

    /**
     * Sets completed.
     *
     * @param completed the completed
     */
    public void setCompleted(int completed) {
        this.completed = completed;
    }

    /**
     * Gets mission type.
     *
     * @return the mission type
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public MissionType getMissionType() {
        return missionType;
    }

    /**
     * Gets hr allowed.
     *
     * @return the hr allowed
     */
    public int getHrAllowed() {
        return hrAllowed;
    }
}
