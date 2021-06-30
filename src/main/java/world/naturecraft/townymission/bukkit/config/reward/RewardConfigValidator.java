package world.naturecraft.townymission.bukkit.config.reward;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigParsingException;
import world.naturecraft.townymission.core.components.enums.RankType;
import world.naturecraft.townymission.core.config.TownyMissionConfig;

public class RewardConfigValidator {

    public static void checkRewardConfig() throws ConfigParsingException {
        // The missionConfig is the main config
        RewardConfigParser.parseAllRewards(RankType.SEASON);
        RewardConfigParser.parseAllRewards(RankType.SPRINT);
    }
}
