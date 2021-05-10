package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.MissionType;

import java.beans.ConstructorProperties;

/**
 * The type Money.
 */
public class Money extends MissionJson {

    /**
     * Instantiates a new Money.
     *
     * @param amount    the amount
     * @param completed the completed
     * @param hrAllowed the hr allowed
     * @param reward    the reward
     */
    @ConstructorProperties({"amount", "completed", "hrAllowed", "reward"})
    public Money(int amount, int completed, int hrAllowed, int reward) {
        super(MissionType.MONEY, amount, completed, hrAllowed, reward);
    }

    /**
     * Parse money.
     *
     * @param json the json
     * @return the money
     * @throws JsonProcessingException the json processing exception
     */
    public static Money parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Money.class);
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
}
