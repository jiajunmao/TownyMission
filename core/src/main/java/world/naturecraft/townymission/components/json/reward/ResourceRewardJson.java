/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.reward;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.utils.Util;

import java.beans.ConstructorProperties;
import java.util.Locale;

/**
 * The type Resource reward json.
 */
public class ResourceRewardJson extends RewardJson {

    @JsonProperty("isMi")
    private boolean isMi;
    @JsonProperty("type")
    private String type;
    @JsonProperty("miID")
    private String miID;
    @JsonProperty("amount")
    private int amount;

    /**
     * Instantiates a new Resource reward json.
     *
     * @param type   the type
     * @param amount the amount
     */
    @JsonIgnore
    public ResourceRewardJson(boolean isMi, String type, String miID, int amount) {
        super(RewardType.RESOURCE);
        this.isMi = isMi;
        this.type = type;
        this.miID = miID;
        this.amount = amount;
    }

    @ConstructorProperties({"type", "amount"})
    public ResourceRewardJson(String type, int amount) {
        super(RewardType.RESOURCE);
        this.isMi = false;
        this.type = type;
        this.miID = null;
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

    @JsonProperty("isMi")
    public boolean isMi() {
        return isMi;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    @JsonGetter("type")
    public String getType() {
        return type;
    }

    @JsonGetter("miID")
    public String getMiID() {
        return miID;
    }

    @JsonGetter("amount")
    public int getAmount() {
        return amount;
    }

    @JsonSetter("amount")
    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @JsonSetter("isMi")
    public void setMi(boolean mi) {
        isMi = mi;
    }

    @JsonSetter("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonSetter("miID")
    public void setMiID(String miID) {
        this.miID = miID;
    }

    @JsonIgnore
    public String getDisplayLine() {
        if (isMi) {
            return "&3" + Util.capitalizeFirst(miID.toLowerCase(Locale.ROOT)) + " &fx" + amount;
        }
        return "&3" + Util.capitalizeFirst(type.toLowerCase(Locale.ROOT)) + " &fx" + amount;
    }
}
