/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The type Expansion.
 */
public class Expansion extends MissionJson {
    private final String world;

    /**
     * Instantiates a new Expansion.
     *
     * @param world     the world
     * @param amount    the amount
     * @param completed the completed
     */
    public Expansion(String world, int amount, int completed, int reward) {
        super(amount, completed, reward);
        this.world = world;
    }

    /**
     * Parse expansion.
     *
     * @param json the json
     * @return the expansion
     */
    public static Expansion parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Expansion.class);
    }
}
