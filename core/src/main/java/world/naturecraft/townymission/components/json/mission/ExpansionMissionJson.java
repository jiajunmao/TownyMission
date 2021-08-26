/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.mission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.services.ChatService;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Expansion.
 */
public class ExpansionMissionJson extends MissionJson {

    @JsonProperty("world")
    private final String world;

    /**
     * Instantiates a new Expansion.
     *
     * @param world         the world
     * @param amount        the amount
     * @param completed     the completed
     * @param hrAllowed     the hr allowed
     * @param reward        the reward
     * @param contributions the contributions
     */
    @ConstructorProperties({"world", "amount", "completed", "hrAllowed", "reward", "contributions"})
    public ExpansionMissionJson(String world, int amount, int completed, int hrAllowed, int reward, Map<String, Integer> contributions) {
        super(MissionType.EXPANSION, amount, completed, hrAllowed, reward, contributions);
        this.world = world;
    }

    /**
     * Parse expansion json.
     *
     * @param json the json
     * @return the expansion json
     * @throws JsonProcessingException the json processing exception
     */
    public static MissionJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, ExpansionMissionJson.class);
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

    /**
     * Get the item lore if using GUI
     *
     * @return The lore in a list of string
     */
    @Override
    @JsonIgnore
    public List<String> getLore() {
        List<String> loreList = TownyMissionInstance.getInstance().getGuiLangEntries("mission_manage.missions.expansion.lores");
        List<String> finalLoreList = new ArrayList<>();
        for (String s : loreList) {
            if (s.contains("Status") || s.contains("Completed"))
                continue;

            finalLoreList.add(ChatService.getInstance().translateColor("&r" + processLore(s).replace("%dimension%", world)));
        }

        return finalLoreList;
    }

    @Override
    @JsonIgnore
    public List<String> getStartedLore() {
        List<String> loreList = TownyMissionInstance.getInstance().getGuiLangEntries("mission_manage.missions.expansion.lores");
        List<String> finalLoreList = new ArrayList<>();
        for (String s : loreList) {
            if (s.contains("Status"))
                continue;

            finalLoreList.add(ChatService.getInstance().translateColor("&r" + processLore(s).replace("%dimension%", world)));
        }

        return finalLoreList;
    }
}
