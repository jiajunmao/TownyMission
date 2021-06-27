/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.components.json.mission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.services.ChatService;
import world.naturecraft.townymission.core.utils.Util;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Resource.
 */
public class ResourceMissionJson extends MissionJson {

    @JsonProperty("mi")
    private final boolean isMi;
    @JsonProperty("type")
    private final String type;

    /**
     * Instantiates a new Resource.
     *
     * @param isMi          the is mi
     * @param type          the type
     * @param amount        the amount
     * @param completed     the completed
     * @param hrAllowed     the hr allowed
     * @param reward        the reward
     * @param contributions the contributions
     */
    @ConstructorProperties({"mi", "type", "amount", "completed", "hrAllowed", "reward", "contributions"})
    public ResourceMissionJson(boolean isMi, String type, int amount, int completed, int hrAllowed, int reward, Map<String, Integer> contributions) {
        super(MissionType.RESOURCE, amount, completed, hrAllowed, reward, contributions);
        this.isMi = isMi;
        this.type = type;
    }

    /**
     * Parse resource.
     *
     * @param json the json
     * @return the resource
     * @throws JsonProcessingException the json processing exception
     */
    public static MissionJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, ResourceMissionJson.class);
    }

    /**
     * Is mi boolean.
     *
     * @return the boolean
     */
    public boolean isMi() {
        return isMi;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the formatted display line when needed to be displayed in MC chat
     *
     * @return the formatted line
     */
    @Override
    @JsonIgnore
    public String getDisplayLine() {
        return "&f- &eisMi: &f" + isMi() + "&7; &e Type: &f" + getType() + "&7; &eAmount: &f" + getAmount() + "&7; &eReward: &f" + getReward();
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
        loreList.add(ChatService.getInstance().translateColor("&r&eItem Type: &7" + Util.capitalizeFirst(type)));
        loreList.add(ChatService.getInstance().translateColor("&r&eAmount: &7" + getAmount()));
        loreList.add(ChatService.getInstance().translateColor("&r&eReward: &7" + getReward()));
        loreList.add(ChatService.getInstance().translateColor("&r&eAllowed Time: &7" + getHrAllowed() + "hr"));

        return loreList;
    }

    @Override
    @JsonIgnore
    public List<String> getStartedLore() {
        List<String> loreList = new ArrayList<>();
        loreList.add(ChatService.getInstance().translateColor("&r&eItem Type: &7" + Util.capitalizeFirst(type)));
        loreList.add(ChatService.getInstance().translateColor("&r&eAmount: &7" + getAmount()));
        loreList.add(ChatService.getInstance().translateColor("&r&eCompleted: &7" + getCompleted()));
        loreList.add(ChatService.getInstance().translateColor("&r&eReward: &7" + getReward()));
        loreList.add(ChatService.getInstance().translateColor("&r&eAllowed Time: &7" + getHrAllowed() + "hr"));

        return loreList;
    }
}
