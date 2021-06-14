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
import java.util.Locale;

/**
 * The type Resource reward json.
 */
public class ResourceRewardJson extends RewardJson {

    @JsonProperty("type")
    private final Material type;
    @JsonProperty("amount")
    private int amount;

    /**
     * Instantiates a new Resource reward json.
     *
     * @param type   the type
     * @param amount the amount
     */
    @ConstructorProperties({"type", "amount"})
    public ResourceRewardJson(Material type, int amount) {
        super(RewardType.RESOURCE);
        this.type = type;
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
        return new ObjectMapper().readValue(json, ResourceRewardJson.class);
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    @JsonProperty("type")
    public Material getType() {
        return type;
    }

    @JsonProperty("amount")
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDisplayLine() {
        return "ResourceType: " + type.name().toLowerCase(Locale.ROOT) +
                ", Amount: " + amount;
    }
}
