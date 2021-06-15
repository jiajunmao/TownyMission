package world.naturecraft.townymission.components.json.cooldown;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.json.rank.RankJson;

import java.beans.ConstructorProperties;
import java.util.Date;

public class CooldownJson {

    @JsonProperty
    private long startedTime;
    @JsonProperty
    private long cooldown;

    @ConstructorProperties({"startedTime", "cooldown"})
    public CooldownJson(long startedTime, long cooldown) {
        this.startedTime = startedTime;
        this.cooldown = cooldown;
    }

    @JsonProperty
    public long getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

    @JsonProperty
    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

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
