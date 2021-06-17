package world.naturecraft.townymission.core.components.json.cooldown;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Cooldown list json.
 */
public class CooldownListJson {

    @JsonProperty("cooldownJsonList")
    private final List<CooldownJson> cooldownJsonList;

    /**
     * Instantiates a new Cooldown list json.
     *
     * @param cooldownJsonList the cooldown json list
     */
    @ConstructorProperties({"cooldownJsonList"})
    public CooldownListJson(List<CooldownJson> cooldownJsonList) {
        this.cooldownJsonList = cooldownJsonList;
    }

    /**
     * Instantiates a new Cooldown list json.
     */
    public CooldownListJson() {
        cooldownJsonList = new ArrayList<>();
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
     * Gets cooldown json list.
     *
     * @return the cooldown json list
     */
    @JsonProperty("cooldownJsonList")
    public List<CooldownJson> getCooldownJsonList() {
        return cooldownJsonList;
    }

    /**
     * Add cooldown.
     *
     * @param cooldownJson the cooldown json
     */
    @JsonIgnore
    public void addCooldown(CooldownJson cooldownJson) {
        cooldownJsonList.add(cooldownJson);
    }

    /**
     * Add cooldown.
     *
     * @param startedTime the started time
     * @param cooldown    the cooldown
     */
    @JsonIgnore
    public void addCooldown(long startedTime, long cooldown) {
        cooldownJsonList.add(new CooldownJson(startedTime, cooldown));
    }

    /**
     * Gets num finished.
     *
     * @param remove the remove
     * @return the num finished
     */
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

    /**
     * Gets num total.
     *
     * @return the num total
     */
    @JsonIgnore
    public int getNumTotal() {
        return cooldownJsonList.size();
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
