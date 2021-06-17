package world.naturecraft.townymission.core.components.json.cooldown;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.ConstructorProperties;
import java.util.Date;

/**
 * The type Cooldown json.
 */
public class CooldownJson {

    @JsonProperty
    private long startedTime;
    @JsonProperty
    private long cooldown;

    /**
     * Instantiates a new Cooldown json.
     *
     * @param startedTime the started time
     * @param cooldown    the cooldown
     */
    @ConstructorProperties({"startedTime", "cooldown"})
    public CooldownJson(long startedTime, long cooldown) {
        this.startedTime = startedTime;
        this.cooldown = cooldown;
    }

    /**
     * Gets started time.
     *
     * @return the started time
     */
    @JsonProperty
    public long getStartedTime() {
        return startedTime;
    }

    /**
     * Sets started time.
     *
     * @param startedTime the started time
     */
    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

    /**
     * Gets cooldown.
     *
     * @return the cooldown
     */
    @JsonProperty
    public long getCooldown() {
        return cooldown;
    }

    /**
     * Sets cooldown.
     *
     * @param cooldown the cooldown
     */
    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    /**
     * Is finished boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    public boolean isFinished() {
        long timeNow = new Date().getTime();
        return (startedTime + cooldown) < timeNow;
    }

    /**
     * Parse rank json.
     *
     * @param json the json
     * @return the rank json
     * @throws JsonProcessingException the json processing exception
     */
    @JsonIgnore
    public static CooldownJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, CooldownJson.class);
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
