package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The type Money.
 */
public class Money extends MissionJson {

    /**
     * Instantiates a new Money.
     *
     * @param completed the completed
     */
    public Money(int amount, int completed, int reward) {
        super(amount, completed, reward);
    }

    /**
     * Parse money.
     *
     * @param json the json
     * @return the money
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
    public String getDisplayLine() {
        return "&f- &eAmount: &f" + getAmount() + "&7; &eReward: &f" + getReward();
    }
}
