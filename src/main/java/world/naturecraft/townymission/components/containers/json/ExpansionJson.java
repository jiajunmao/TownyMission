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
import java.util.Map;

/**
 * The type Expansion.
 */
public class ExpansionJson extends MissionJson {

    @JsonProperty("world")
    private final String world;

    /**
     * Instantiates a new Expansion.
     *
     * @param world     the world
     * @param amount    the amount
     * @param completed the completed
     * @param hrAllowed the hr allowed
     * @param reward    the reward
     */
    @ConstructorProperties({"world", "amount", "completed", "hrAllowed", "reward", "contributions"})
    public ExpansionJson(String world, int amount, int completed, int hrAllowed, int reward, Map<String, Integer> contributions) {
        super(MissionType.EXPANSION, amount, completed, hrAllowed, reward, contributions);
        this.world = world;
    }

    public static ExpansionJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, ExpansionJson.class);
    }

    /**
     * Gets world.
     *
     * @return the world
     */
    public String getWorld() {
        return world;
    }

    /**
     * Get the formatted display line when needed to be displayed in MC chat
     *
     * @return the formatted line
     */
    @Override
    @JsonIgnore
    public String getDisplayLine() {
        return "&f- &eWorld: &f" + getWorld() + "&7; &eAmount: &f" + getAmount() + "&7; &eReward: &f" + getReward();
    }
}
