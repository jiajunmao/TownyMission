package world.naturecraft.townymission.core.components.json.cooldown;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class CooldownListJson {

    @JsonProperty("cooldownJsonList")
    private List<CooldownJson> cooldownJsonList;

    @ConstructorProperties({"cooldownJsonList"})
    public CooldownListJson(List<CooldownJson> cooldownJsonList) {
        this.cooldownJsonList = cooldownJsonList;
    }

    public CooldownListJson() {
        cooldownJsonList = new ArrayList<>();
    }

    @JsonProperty("cooldownJsonList")
    public List<CooldownJson> getCooldownJsonList() {
        return cooldownJsonList;
    }

    @JsonIgnore
    public void addCooldown(CooldownJson cooldownJson) {
        cooldownJsonList.add(cooldownJson);
    }

    @JsonIgnore
    public void addCooldown(long startedTime, long cooldown) {
        cooldownJsonList.add(new CooldownJson(startedTime, cooldown));
    }

    @JsonIgnore
    public int getNumFinished(boolean remove) {
        int num = 0;
        List<CooldownJson> removing = new ArrayList<>();
        for (CooldownJson cooldownJson : cooldownJsonList) {
            if (cooldownJson.isFinished()) {
                num++;
                removing.add(cooldownJson);
            }
        }

        if (remove) {
            cooldownJsonList.removeAll(removing);
        }

        return num;
    }

    @JsonIgnore
    public int getNumTotal() {
        return cooldownJsonList.size();
    }

    /**
     * Parse rank json.
     *
     * @param json the json
     * @return the rank json
     * @throws JsonProcessingException the json processing exception
     */
    @JsonIgnore
    public static CooldownListJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, CooldownListJson.class);
    }

    /**
     * To json string.
     *
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
    @JsonIgnore
    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
