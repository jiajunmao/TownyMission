package world.naturecraft.townymission.config.reward;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.reward.*;

/**
 * The type Reward json factory.
 */
public class RewardJsonFactory {

    /**
     * Gets json.
     *
     * @param rewardJson the reward json
     * @param rewardType the reward type
     * @return the json
     * @throws JsonProcessingException the json processing exception
     */
    public static RewardJson getJson(String rewardJson, RewardType rewardType) throws JsonProcessingException {
        switch (rewardType) {
            case COMMAND:
                return CommandRewardJson.parse(rewardJson);
            case POINTS:
                return PointRewardJson.parse(rewardJson);
            case MONEY:
                return MoneyRewardJson.parse(rewardJson);
            case RESOURCE:
                return ResourceRewardJson.parse(rewardJson);
            default:
                return null;
        }
    }
}
