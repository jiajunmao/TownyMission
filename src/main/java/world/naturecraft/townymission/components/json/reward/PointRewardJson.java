package world.naturecraft.townymission.components.json.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.RewardType;

import java.beans.ConstructorProperties;


/**
 * The type Point reward json.
 */
// Well technically this is not a json, but I guess I will put it here
public class PointRewardJson extends RewardJson {

    @JsonProperty("amount")
    private int amount;

    /**
     * Instantiates a new Point reward json.
     *
     * @param amount the amount
     */
    @ConstructorProperties({"amount"})
    public PointRewardJson(int amount) {
        super(RewardType.POINTS);
        this.amount = amount;
    }

    /**
     * Parse reward json.
     *
     * @param json the json
     * @return the reward json
     * @throws JsonProcessingException the json processing exception
     */
    @JsonIgnore
    public static RewardJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, PointRewardJson.class);
    }

    @JsonProperty("amount")
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @JsonIgnore
    public String getDisplayLine() {
        return "Amount: " + amount;
    }
}
