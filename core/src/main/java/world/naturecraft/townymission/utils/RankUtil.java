/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.utils;

import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.components.entity.SeasonEntry;
import world.naturecraft.townymission.components.entity.SprintEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Rank util.
 */
public class RankUtil {
    /**
     * Sort list.
     *
     * @param list the list
     */
    public static List<? extends Rankable> sort(List<? extends Rankable> list) {
        Collections.sort(list);
        return list;
    }
}
