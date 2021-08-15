package world.naturecraft.townymission.components.json.cooldown;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.naturelib.exceptions.DataProcessException;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Cooldown list json.
 */
public class CooldownListJson {

    //WARNING: consider the case where the server active cooldownJsonList

    @JsonProperty("cooldownJsonList")
    private List<CooldownJson> cooldownJsonList;
    @JsonProperty("cooldownMap")
    private Map<Integer, CooldownJson> cooldownMap;

    /**
     * Instantiates a new Cooldown list json.
     *
     * @param cooldownJsonList the cooldown json list
     */
    @ConstructorProperties({"cooldownJsonList", "cooldownMap"})
    public CooldownListJson(List<CooldownJson> cooldownJsonList, Map<Integer, CooldownJson> cooldownMap) {
        this.cooldownJsonList = cooldownJsonList;
        this.cooldownMap = cooldownMap;
    }

    /**
     * Instantiates a new Cooldown list json.
     */
    public CooldownListJson() {
        cooldownJsonList = new ArrayList<>();
        cooldownMap = new HashMap<>();
    }

    /**
     * Parse rank json.
     *
     * @param json the json
     * @return the rank json
     */
    @JsonIgnore
    public static CooldownListJson parse(String json) throws DataProcessException {
        try {
            return new ObjectMapper().readValue(json, CooldownListJson.class);
        } catch (JsonProcessingException e) {
            throw new DataProcessException(e);
        }

    }

    @JsonProperty("cooldownMap")
    public Map<Integer, CooldownJson> getCooldownMap() {
        return cooldownMap;
    }


    @JsonIgnore
    public void addCooldown(int numMission, CooldownJson cooldownJson) {
        cooldownMap.put(numMission, cooldownJson);
    }

    public boolean isFinished(int numMission) {
        return cooldownMap.get(numMission).isFinished();
    }

    /**
     * To json string.
     *
     * @return the string
     * @throws DataProcessException the json processing exception
     */
    @JsonIgnore
    public String toJson() throws DataProcessException {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new DataProcessException(e);
        }
    }
}
