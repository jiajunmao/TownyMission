/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.json.mission;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * The type Mob.
 */
public class MobMissionJson extends MissionJson {

    @JsonProperty("isMm")
    private final boolean isMm;
    @JsonProperty("entityType")
    private final String entityType;

    /**
     * Instantiates a new Mob.
     *
     * @param entityType    the entity type
     * @param amount        the amount
     * @param completed     the completed
     * @param hrAllowed     the hr allowed
     * @param reward        the reward
     * @param contributions the contributions
     */
    @ConstructorProperties({"isMm", "entityType", "amount", "completed", "hrAllowed", "reward", "contributions"})
    public MobMissionJson(boolean isMm, String entityType, int amount, int completed, int hrAllowed, int reward, Map<String, Integer> contributions) {
        super(MissionType.MOB, amount, completed, hrAllowed, reward, contributions);
        this.isMm = isMm;
        this.entityType = entityType;
    }

    /**
     * Parse mob.
     *
     * @param json the json
     * @return the mob
     * @throws JsonProcessingException the json processing exception
     */
    public static MissionJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, MobMissionJson.class);
    }

    /**
     * Gets entity type.
     *
     * @return the entity type
     */
    public String getEntityType() {
        return entityType;
    }


    /**
     * Get the formatted display line when needed to be displayed in MC chat
     *
     * @return the formatted line
     */
    @Override
    @JsonIgnore
    public String getDisplayLine() {
        return "&f- &eisMm: &f" + isMm + " &eType: &f" + getEntityType() + "&7; &eAmount: &f" + getAmount() + "&7; &eReward: &f" + getReward();
    }

    /**
     * Get the item lore if using GUI
     *
     * @return The lore in a list of string
     */
    @Override
    @JsonIgnore
    public List<String> getLore() {
        List<String> loreList = TownyMissionInstance.getInstance().getGuiLangEntries("mission_manage.missions.mob.lores");
        List<String> finalLoreList = new ArrayList<>();
        for (String s : loreList) {
            if (s.contains("Status") || s.contains("Completed"))
                continue;

            if (isMm)
                finalLoreList.add(ChatService.getInstance().translateColor("&r" + processLore(s).replace("%type%", Util.mmDispalyName(entityType))));
            else
                finalLoreList.add(ChatService.getInstance().translateColor("&r" + processLore(s).replace("%type%", Util.capitalizeFirst(entityType))));
        }

        return finalLoreList;
    }

    @Override
    @JsonIgnore
    public List<String> getStartedLore() {
        List<String> loreList = TownyMissionInstance.getInstance().getGuiLangEntries("mission_manage.missions.mob.lores");
        List<String> finalLoreList = new ArrayList<>();
        for (String s : loreList) {
            if (s.contains("Status"))
                continue;

            if (isMm)
                finalLoreList.add(ChatService.getInstance().translateColor("&r" + processLore(s).replace("%type%", Util.mmDispalyName(entityType))));
            else
                finalLoreList.add(ChatService.getInstance().translateColor("&r" + processLore(s).replace("%type%", Util.capitalizeFirst(entityType))));
        }

        return finalLoreList;
    }

    @JsonGetter("isMm")
    public boolean isMm() {
        return isMm;
    }
}
