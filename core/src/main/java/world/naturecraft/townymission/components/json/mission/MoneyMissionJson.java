/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.mission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.utils.Util;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Money.
 */
public class MoneyMissionJson extends MissionJson {

    private final boolean returnable;

    /**
     * Instantiates a new Money.
     *
     * @param amount        the amount
     * @param completed     the completed
     * @param hrAllowed     the hr allowed
     * @param reward        the reward
     * @param contributions the contributions
     */
    @ConstructorProperties({"amount", "completed", "hrAllowed", "reward", "returnable", "contributions"})
    public MoneyMissionJson(int amount, int completed, int hrAllowed, int reward, boolean returnable, Map<String, Integer> contributions) {
        super(MissionType.MONEY, amount, completed, hrAllowed, reward, contributions);
        this.returnable = returnable;
    }

    /**
     * Parse money.
     *
     * @param json the json
     * @return the money
     * @throws JsonProcessingException the json processing exception
     */
    public static MissionJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, MoneyMissionJson.class);
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
        List<String> loreList = TownyMissionInstance.getInstance().getGuiLangEntries("mission_manage.missions.money.lores");
        List<String> finalLoreList = new ArrayList<>();
        for (String s : loreList) {
            if (s.contains("Status") || s.contains("Completed"))
                continue;

            finalLoreList.add(ChatService.getInstance().translateColor("&r" + processLore(s).replace("%returnable%", String.valueOf(returnable))));
        }

        return finalLoreList;
    }

    @Override
    @JsonIgnore
    public List<String> getStartedLore() {
        List<String> loreList = TownyMissionInstance.getInstance().getGuiLangEntries("mission_manage.missions.money.lores");
        List<String> finalLoreList = new ArrayList<>();
        for (String s : loreList) {
            if (s.contains("Status"))
                continue;

            finalLoreList.add(ChatService.getInstance().translateColor("&r" + processLore(s).replace("%returnable%", String.valueOf(returnable))));
        }

        return finalLoreList;
    }

    public boolean isReturnable() {
        return returnable;
    }
}
