/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.reward;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.RewardType;

import java.beans.ConstructorProperties;

public class MoneyRewardJson extends RewardJson {

    @JsonProperty("amount")
    private int amount;

    @ConstructorProperties({"amount"})
    public MoneyRewardJson(int amount) {
        super(RewardType.MONEY);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public static RewardJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, MoneyRewardJson.class);
    }

}
