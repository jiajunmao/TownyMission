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
import world.naturecraft.townymission.components.json.reward.RewardJson;
import world.naturecraft.townymission.utils.RewardJsonFactory;
import world.naturecraft.townymission.utils.Util;

import java.util.*;

public class RewardConfigParser {

    public static List<RewardJson> parseAllRewards(RankType rankType) {
        TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
        List<String> rankList = instance.getConfig().getStringList(rankType.name().toLowerCase(Locale.ROOT) + ".rewards");
        List<RewardJson> rewardJsonList = new ArrayList<>();

        for (String rank : rankList) {
            String fullpath = rankType.name().toLowerCase(Locale.ROOT) + ".rewards." + rank;
            List<String> rankedRewardList = instance.getConfig().getStringList(fullpath);

            for (String rankedReward : rankedRewardList) {
                int middleIdx = rankedReward.indexOf("{");
                RewardType rewardType = RewardType.valueOf(rankedReward.substring(0, middleIdx).toUpperCase(Locale.ROOT));
                String actualRewardString = rankedReward.substring(middleIdx+1);

                try {
                    RewardJson rewardJson = RewardJsonFactory.getJson(actualRewardString, rewardType);
                    if (Util.isInt(rank)) {
                        rewardJson.setRank(Integer.parseInt(rank));
                    } else {
                        rewardJson.setRank(-1);
                    }

                    rewardJsonList.add(rewardJson);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }

        }

        return rewardJsonList;
    }

    public static Map<Integer, List<RewardJson>> getRankRewardsMap(RankType rankType) {
        List<RewardJson> allRewards = parseAllRewards(rankType);
        Map<Integer, List<RewardJson>> rewardsMap = new HashMap<>();

        for (RewardJson rewardJson : allRewards) {
            int currentRank = rewardJson.getRank();
            if (!rewardsMap.containsKey(currentRank)) {
                rewardsMap.put(currentRank, new ArrayList<>());
            }

            rewardsMap.get(currentRank).add(rewardJson);
        }

        return rewardsMap;
    }

    public int getNumDefinedRanks(RankType rankType) {
        List<RewardJson> allRewards = parseAllRewards(rankType);
        Set<Integer> ranks = new HashSet<>();

        for (RewardJson rewardJson : allRewards) {
            ranks.add(rewardJson.getRank());
        }

        return ranks.size();
    }

    public static RewardMethod getRewardMethods(RankType rankType) {
        TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
        if (rankType.equals(RankType.SEASON)) {
            return RewardMethod.valueOf(instance.getConfig().getString("season.rewards.method"));
        } else {
            return RewardMethod.valueOf(instance.getConfig().getString("sprint.rewards.method"));
        }
    }

    public static RewardType getRewardType(String rewardString) {
        int endIdx = rewardString.indexOf("{");
        String type = rewardString.substring(0, endIdx);
        return RewardType.valueOf(type.toUpperCase(Locale.ROOT));
    }
}
