/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.RewardMethod;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.mission.MissionJson;

public abstract class RewardJson {

    @JsonIgnore
    private RewardType rewardType;

    public RewardJson(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public static RewardJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, RewardJson.class);
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
