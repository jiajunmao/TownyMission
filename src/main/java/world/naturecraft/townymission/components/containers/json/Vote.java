package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

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
    public Vote(int amount, int completed, int reward) {
        super(amount, completed, reward);
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
