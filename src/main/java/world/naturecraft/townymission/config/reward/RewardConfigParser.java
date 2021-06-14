/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.config.reward;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.enums.RewardMethod;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.reward.RewardJson;
import world.naturecraft.townymission.utils.RewardJsonFactory;
import world.naturecraft.townymission.utils.Util;

import java.util.*;

/**
 * The type Reward config parser.
 */
public class RewardConfigParser {

    /**
     * Parse all rewards list.
     *
     * @param rankType the rank type
     * @return the list
     */
    public static List<RewardJson> parseAllRewards(RankType rankType) {
        TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
        ConfigurationSection rankConfigSection = instance.getConfig().getConfigurationSection(rankType.name().toLowerCase(Locale.ROOT) + ".rewards.rewards");
        Set<String> rankList = rankConfigSection.getKeys(false);
        System.out.println("Rank String List length: " + rankList.size());
        List<RewardJson> rewardJsonList = new ArrayList<>();

        for (String rank : rankList) {
            String fullpath = rankType.name().toLowerCase(Locale.ROOT) + ".rewards.rewards." + rank;
            List<String> rankedRewardList = instance.getConfig().getStringList(fullpath);
            System.out.println("Parsing reward - Rank: " + rank + ", reward list lenght: " + rankedRewardList.size());

            for (String rankedReward : rankedRewardList) {
                System.out.println("Parsing RewardJson: " + rankedReward);
                int middleIdx = rankedReward.indexOf("{");
                RewardType rewardType = RewardType.valueOf(rankedReward.substring(0, middleIdx).toUpperCase(Locale.ROOT));
                String actualRewardString = rankedReward.substring(middleIdx);

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

    /**
     * Gets rank rewards map.
     *
     * @param rankType the rank type
     * @return the rank rewards map
     */
    public static Map<Integer, List<RewardJson>> getRankRewardsMap(RankType rankType) {
        List<RewardJson> allRewards = parseAllRewards(rankType);
        System.out.println("RewardJson list length: " + allRewards.size());
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

    /**
     * Gets reward methods.
     *
     * @param rankType the rank type
     * @return the reward methods
     */
    public static RewardMethod getRewardMethods(RankType rankType) {
        TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
        if (rankType.equals(RankType.SEASON)) {
            return RewardMethod.valueOf(instance.getConfig().getString("season.rewards.method"));
        } else {
            return RewardMethod.valueOf(instance.getConfig().getString("sprint.rewards.method"));
        }
    }

    /**
     * Gets reward type.
     *
     * @param rewardString the reward string
     * @return the reward type
     */
    public static RewardType getRewardType(String rewardString) {
        int endIdx = rewardString.indexOf("{");
        String type = rewardString.substring(0, endIdx);
        return RewardType.valueOf(type.toUpperCase(Locale.ROOT));
    }

    /**
     * Gets num defined ranks.
     *
     * @param rankType the rank type
     * @return the num defined ranks
     */
    public int getNumDefinedRanks(RankType rankType) {
        List<RewardJson> allRewards = parseAllRewards(rankType);
        Set<Integer> ranks = new HashSet<>();

        for (RewardJson rewardJson : allRewards) {
            ranks.add(rewardJson.getRank());
        }

        return ranks.size();
    }
}
