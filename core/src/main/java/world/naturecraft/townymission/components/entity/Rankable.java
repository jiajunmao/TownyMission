/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.entity;

import world.naturecraft.townymission.TownyMissionInstance;

import java.util.Collections;
import java.util.List;

/**
 * The interface Rankable.
 */
public interface Rankable extends Comparable<Rankable> {

    /**
     * Gets ranking points.
     *
     * @param numResident the num resident
     * @param naturePoint the nature point
     * @param instance    the instance
     * @return the ranking points
     */
    static int getRankingPoints(int numResident, int naturePoint, TownyMissionInstance instance) {
        int baseline = instance.getInstanceConfig().getInt("participants.sprintRewardBaseline");
        int memberScale = instance.getInstanceConfig().getInt("participants.sprintRewardMemberScale");
        int baselineCap = instance.getInstanceConfig().getInt("participants.sprintRewardBaselineCap");
        int increment = instance.getInstanceConfig().getInt("participants.sprintBaselineIncrement");
        int currentSprint = instance.getStatsConfig().getInt("sprint.current");

        int realBaseline = Math.min(baseline + memberScale * Math.max(0, numResident - 1), baselineCap) + increment * (currentSprint - 1);

        return Math.max(0, (naturePoint - realBaseline) / numResident);
    }

    /**
     * Gets point.
     *
     * @return the point
     */
    int getRankingFactor();

    String getRankingId();
}
