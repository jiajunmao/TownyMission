/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.mission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.naturelib.exceptions.ConfigParsingException;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The type Json entry.
 */
public abstract class MissionJson {

    @JsonIgnore
    private final MissionType missionType;
    @JsonProperty("reward")
    private final int reward;
    @JsonProperty("hrAllowed")
    private final int hrAllowed;
    @JsonProperty("amount")
    private final int amount;
    @JsonProperty("contributions")
    private final Map<String, Integer> contributions;
    @JsonProperty("completed")
    private int completed;

    /**
     * Instantiates a new Mission json.
     *
     * @param missionType   the mission type
     * @param amount        the amount
     * @param completed     the completed
     * @param hrAllowed     the hr allowed
     * @param reward        the reward
     * @param contributions the contributions
     */
    protected MissionJson(MissionType missionType, int amount, int completed, int hrAllowed, int reward, Map<String, Integer> contributions) {
        this.missionType = missionType;
        this.reward = reward;
        this.amount = amount;
        this.completed = completed;
        this.hrAllowed = hrAllowed;
        if (contributions == null) {
            this.contributions = new HashMap<>();
        } else {
            this.contributions = contributions;
        }
    }

    /**
     * Parse mission json.
     *
     * @param json the json
     * @return the mission json
     * @throws JsonProcessingException the json processing exception
     */
    public static MissionJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, MissionJson.class);
    }

    /**
     * To json string.
     *
     * @return the string
     * @throws ConfigParsingException the json processing exception
     */
    public String toJson() throws ConfigParsingException {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new ConfigParsingException(e);
        }
    }

    /**
     * Get the formatted display line when needed to be displayed in MC chat
     *
     * @return the formatted line
     */
    @JsonIgnore
    public abstract String getDisplayLine();

    /**
     * Get the item lore if using GUI
     *
     * @return The lore in a list of string
     */
    @JsonIgnore
    public abstract List<String> getLore();

    @JsonIgnore
    public abstract List<String> getStartedLore();

    /**
     * Gets reward.
     *
     * @return the reward
     */
    public int getReward() {
        return reward;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Gets completed.
     *
     * @return the completed
     */
    public int getCompleted() {
        return completed;
    }

    /**
     * Sets completed.
     *
     * @param completed the completed
     */
    public void setCompleted(int completed) {
        this.completed = completed;
    }

    /**
     * Add completed.
     *
     * @param amount the amount
     */
    public void addCompleted(int amount) {
        this.completed += amount;
    }

    /**
     * Gets mission type.
     *
     * @return the mission type
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public MissionType getMissionType() {
        return missionType;
    }

    /**
     * Gets hr allowed.
     *
     * @return the hr allowed
     */
    public int getHrAllowed() {
        return hrAllowed;
    }

    /**
     * Add contribution.
     *
     * @param playerUUID the player uuid
     * @param amount     the amount
     */
    public void addContribution(String playerUUID, int amount) {
        if (contributions.containsKey(playerUUID)) {
            contributions.put(playerUUID, contributions.get(playerUUID) + amount);
        } else {
            contributions.put(playerUUID, amount);
        }
    }

    /**
     * Remove contribution.
     *
     * @param playerUUID the player uuid
     * @param amount     the amount
     */
    public void removeContribution(String playerUUID, int amount) {
        if (contributions.containsKey(playerUUID)) {
            contributions.put(playerUUID, contributions.get(playerUUID) - amount);
        }
    }

    /**
     * Remove contributions.
     *
     * @param playerUUID the player uuid
     */
    public void removeContributions(String playerUUID) {
        contributions.remove(playerUUID);
    }

    /**
     * Gets contributions.
     *
     * @return the contributions
     */
    public Map<String, Integer> getContributions() {
        return contributions;
    }

    @JsonIgnore
    public MissionEntry getNewMissionEntry(UUID townUUID, int numMission) {
        MissionEntry entry = new MissionEntry(
                UUID.randomUUID(),
                getMissionType().name(),
                new Date().getTime(),
                0,
                TimeUnit.MILLISECONDS.convert(getHrAllowed(), TimeUnit.HOURS),
                toJson(),
                townUUID,
                null,
                numMission);
        return entry;
    }

    protected String processLore(String s) {
        System.out.println("Processing " + s);
        String result = s.replace("%amount%", String.valueOf(getAmount()))
                .replace("%completed%", String.valueOf(getCompleted()))
                .replace("%reward%", String.valueOf(getReward()))
                .replace("%time%", String.valueOf(getHrAllowed()));
        System.out.println("To be " + result);
        return result;
    }
}
