/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.components.json.mission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.services.ChatService;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Vote.
 */
public class VoteMissionJson extends MissionJson {

    /**
     * Instantiates a new Vote.
     *
     * @param amount        the amount
     * @param completed     the completed
     * @param hrAllowed     the hr allowed
     * @param reward        the reward
     * @param contributions the contributions
     */
    @ConstructorProperties({"amount", "completed", "hrAllowed", "reward", "contributions"})
    public VoteMissionJson(int amount, int completed, int hrAllowed, int reward, Map<String, Integer> contributions) {
        super(MissionType.VOTE, amount, completed, hrAllowed, reward, contributions);
    }

    /**
     * Parse vote.
     *
     * @param json the json
     * @return the vote
     * @throws JsonProcessingException the json processing exception
     */
    public static MissionJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, VoteMissionJson.class);
    }

    /**
     * Get the formatted display line when needed to be displayed in MC chat
     *
     * @return the formatted line
     */
    @Override
    @JsonIgnore
    public String getDisplayLine() {
        return "&f- &eAmount: &f" + getAmount() + "&7; &eReward: &f" + getReward();
    }

    /**
     * Get the item lore if using GUI
     *
     * @return The lore in a list of string
     */
    @Override
    @JsonIgnore
    public List<String> getLore() {
        List<String> loreList = new ArrayList<>();
        loreList.add(ChatService.getInstance().translateColor("&r&eAmount: &7" + getAmount()));
        loreList.add(ChatService.getInstance().translateColor("&r&eReward: &7" + getReward()));
        loreList.add(ChatService.getInstance().translateColor("&r&eAllowed Time: &7" + getHrAllowed() + "hr"));

        return loreList;
    }
}
