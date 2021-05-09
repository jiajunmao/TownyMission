/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.MissionType;

import java.beans.ConstructorProperties;

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

    public int getReward() {
        return reward;
    }

    public int getAmount() {
        return amount;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public MissionType getMissionType() {
        return missionType;
    }

    public int getHrAllowed() {
        return hrAllowed;
    }
}
