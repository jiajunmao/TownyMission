/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import world.naturecraft.townymission.components.enums.RewardType;

import java.beans.ConstructorProperties;

public class ResourceRewardJson extends RewardJson {

    @JsonProperty("type")
    private Material type;
    @JsonProperty("amount")
    private int amount;

    @ConstructorProperties({"type", "amount"})
    public ResourceRewardJson(Material type, int amount) {
        super(RewardType.RESOURCE);
        this.type = type;
        this.amount = amount;
    }

    @JsonIgnore
    public static RewardJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, ResourceRewardJson.class);
    }

    @JsonProperty("type")
    public Material getType() {
        return type;
    }

    @JsonProperty("amount")
    public int getAmount() {
        return amount;
    }
}
