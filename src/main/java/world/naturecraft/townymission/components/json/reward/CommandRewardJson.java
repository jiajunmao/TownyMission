/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.RewardType;

import java.beans.ConstructorProperties;

/**
 * The type Command reward json.
 */
public class CommandRewardJson extends RewardJson {

    @JsonProperty("command")
    private final String command;

    /**
     * Instantiates a new Command reward json.
     *
     * @param command the command
     */
    @ConstructorProperties({"command"})
    public CommandRewardJson(String command) {
        super(RewardType.COMMAND);
        this.command = command;
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
        return new ObjectMapper().readValue(json, CommandRewardJson.class);
    }

    /**
     * Gets command.
     *
     * @return the command
     */
    @JsonProperty("command")
    public String getCommand() {
        return command;
    }

    @JsonIgnore
    public String getDisplayLine() {
        return "Command: " + command;
    }

    @JsonIgnore
    public int getAmount() {
        return -1;
    }

    @JsonIgnore
    public void setAmount(int amount) {
        // Do nothing
    }
}
