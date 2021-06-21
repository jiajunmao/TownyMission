/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.components.json.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.core.components.enums.RewardType;

import java.beans.ConstructorProperties;

/**
 * The type Money reward json.
 */
public class MoneyRewardJson extends RewardJson {

    @JsonProperty("amount")
    private int amount;

    /**
     * Instantiates a new Money reward json.
     *
     * @param amount the amount
     */
    @ConstructorProperties({"amount"})
    public MoneyRewardJson(int amount) {
        super(RewardType.MONEY);
        this.amount = amount;
    }

    /**
     * Parse reward json.
     *
     * @param json the json
     * @return the reward json
     * @throws JsonProcessingException the json processing exception
     */
    @JsonIgnore
    public static RewardJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, MoneyRewardJson.class);
    }

    @JsonProperty("amount")
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @JsonIgnore
    public String getDisplayLine() {
        return "Amount: " + amount;
    }
}
