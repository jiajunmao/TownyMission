/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The type Json entry.
 */
public abstract class MissionJson {

    private final int reward;
    private final int amount;
    private final int completed;

    protected MissionJson(int amount, int completed, int reward) {
        this.reward = reward;
        this.amount = amount;
        this.completed = completed;
    }

    /**
     * Gets json.
     *
     * @return the json
     * @throws JsonProcessingException the json processing exception
     */
    public JsonObject getJson() throws JsonProcessingException {
        return new JsonParser().parse(new ObjectMapper().writeValueAsString(this)).getAsJsonObject();
    }

    public String toString() {
        try {
            return getJson().toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
