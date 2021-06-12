/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config.reward;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.enums.RewardMethod;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.reward.CommandRewardJson;
import world.naturecraft.townymission.components.json.reward.MoneyRewardJson;
import world.naturecraft.townymission.components.json.reward.RewardJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RewardConfigParser {

    public static List<RewardJson> parseAll(RankType rankType) {
        TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
        List<String> rewardList;
        if (rankType.equals(RankType.SPRINT)) {
            rewardList = instance.getConfig().getStringList("sprint.rewards.rewards");
        } else {
            rewardList = instance.getConfig().getStringList("season.rewards.rewards");
        }

        List<RewardJson> rewardJsonList = new ArrayList<>();
        for (String reward : rewardList) {
            try {
                String actualReward = reward.substring(reward.indexOf("{"));
                rewardJsonList.add(RewardJson.parse(actualReward));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return rewardJsonList;
    }

    public static RewardMethod getRewardMethod(RankType rankType) {
        TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
        if (rankType.equals(RankType.SEASON)) {
            return RewardMethod.valueOf(instance.getConfig().getString("season.rewards.method"));
        } else {
            return RewardMethod.valueOf(instance.getConfig().getString("sprint.rewards.method"));
        }
    }

    private static RewardType getRewardType(String rewardString) {
        int endIdx = rewardString.indexOf("{");
        String type = rewardString.substring(0, endIdx);
        return RewardType.valueOf(type.toUpperCase(Locale.ROOT));
    }
}
