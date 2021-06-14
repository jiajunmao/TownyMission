package world.naturecraft.townymission.components.json.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.utils.Util;

import java.beans.ConstructorProperties;


// Well technically this is not a json, but I guess I will put it here
public class PointRewardJson extends RewardJson {

    @JsonProperty("amount")
    private int amount;

    @ConstructorProperties({"amount"})
    public PointRewardJson(int amount) {
        super(RewardType.POINTS);
        this.amount = amount;
    }

    @JsonProperty("amount")
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDisplayLine() {
        return "Amount: " + amount;
    }

    @JsonIgnore
    public static RewardJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, PointRewardJson.class);
    }
}
