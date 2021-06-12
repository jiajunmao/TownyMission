package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.Util;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Money.
 */
public class MoneyJson extends MissionJson {

    /**
     * Instantiates a new Money.
     *
     * @param amount        the amount
     * @param completed     the completed
     * @param hrAllowed     the hr allowed
     * @param reward        the reward
     * @param contributions the contributions
     */
    @ConstructorProperties({"amount", "completed", "hrAllowed", "reward", "contributions"})
    public MoneyJson(int amount, int completed, int hrAllowed, int reward, Map<String, Integer> contributions) {
        super(MissionType.MONEY, amount, completed, hrAllowed, reward, contributions);
    }

    /**
     * Parse money.
     *
     * @param json the json
     * @return the money
     * @throws JsonProcessingException the json processing exception
     */
    public static MissionJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, MoneyJson.class);
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
     * Get the item lore if using GUI
     *
     * @return The lore in a list of string
     */
    @Override
    @JsonIgnore
    public List<String> getLore() {
        List<String> loreList = new ArrayList<>();
        loreList.add(Util.translateColor("&r&eAmount: &7" + getAmount()));
        loreList.add(Util.translateColor("&r&eReward: &7" + getReward()));
        loreList.add(Util.translateColor("&r&eAllowed Time: &7" + getHrAllowed() + "hr"));

        return loreList;
    }
}
