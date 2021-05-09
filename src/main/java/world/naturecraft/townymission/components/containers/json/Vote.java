package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.MissionType;

import java.beans.ConstructorProperties;

/**
 * The type Vote.
 */
public class Vote extends MissionJson {

    /**
     * Instantiates a new Vote.
     *
     * @param amount    the amount
     * @param completed the completed
     */
    @ConstructorProperties({"amount", "completed", "hrAllowed", "reward"})
    public Vote(int amount, int completed, int hrAllowed, int reward) {
        super(MissionType.VOTE, amount, completed, hrAllowed, reward);
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
     * Parse vote.
     *
     * @param json the json
     * @return the vote
     */
    public static Vote parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Vote.class);
    }
}
