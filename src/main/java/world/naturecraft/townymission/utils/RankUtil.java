/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.utils;

import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.api.exceptions.NotFoundException;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.data.dao.Dao;

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
     * @return the list
     */
    public static List<? extends Rankable> sort(List<? extends Rankable> list) {
        Collections.sort(list);
        return list;
    }
}
