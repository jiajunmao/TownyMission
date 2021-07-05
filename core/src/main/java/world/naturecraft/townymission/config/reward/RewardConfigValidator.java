package world.naturecraft.townymission.config.reward;

import world.naturecraft.townymission.api.exceptions.ConfigParsingException;
import world.naturecraft.townymission.components.enums.RankType;

public class RewardConfigValidator {

    public static void checkRewardConfig() throws ConfigParsingException {
        // The missionConfig is the main config
        RewardConfigParser.parseAllRewards(RankType.SEASON);
        RewardConfigParser.parseAllRewards(RankType.SPRINT);
    }
}
