/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.components.enums.RewardType;

import java.beans.ConstructorProperties;

public class CommandRewardJson extends RewardJson {

    @JsonProperty("command")
    private String command;

    @ConstructorProperties({"command"})
    public CommandRewardJson(String command) {
        super(RewardType.COMMAND);
        this.command = command;
    }

    @JsonProperty("command")
    public String getCommand() {
        return command;
    }

    @JsonIgnore
    public static RewardJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, CommandRewardJson.class);
    }

    public String getDisplayLine() {
        return "Command: " + command;
    }

    public int getAmount() {
        return -1;
    }

    public void setAmount(int amount) {
        // Do nothing
    }
}
