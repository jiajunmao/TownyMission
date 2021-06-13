package world.naturecraft.townymission.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.reward.*;

public class RewardJsonFactory {

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
