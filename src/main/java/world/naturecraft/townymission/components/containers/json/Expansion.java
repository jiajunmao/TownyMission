/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

import com.google.gson.JsonObject;

/**
 * The type Expansion.
 */
public class Expansion extends JsonEntry {
    private final String world;
    private final int amount;
    private final int completed;

    /**
     * Instantiates a new Expansion.
     *
     * @param world     the world
     * @param amount    the amount
     * @param completed the completed
     */
    public Expansion(String world, int amount, int completed) {
        this.world = world;
        this.amount = amount;
        this.completed = completed;
    }

    /**
     * Parse expansion.
     *
     * @param json the json
     * @return the expansion
     */
    public static Expansion parse(JsonObject json) {
        return new Expansion(json.get("world").toString(),
                Integer.parseInt(json.get("amount").toString()),
                Integer.parseInt(json.get("completed").toString()));
    }
}
