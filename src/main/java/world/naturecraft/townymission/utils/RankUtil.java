/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.utils;

import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.api.exceptions.NotFoundException;
import world.naturecraft.townymission.components.containers.entity.Rankable;
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

    /**
     * Gets rank.
     *
     * @param town the town
     * @param dao  the dao
     * @return the rank
     * @throws NotFoundException the not found exception
     */
    public static int getRank(Town town, Dao<? extends Rankable> dao) throws NotFoundException {
        List<Rankable> list = (List<Rankable>) dao.getEntries();
        Collections.sort(list);
        int index = 1;
        for (Rankable rankable : list) {
            if (rankable.getID().equalsIgnoreCase(town.getUUID().toString())) {
                return index;
            }
            index++;
        }

        throw new NotFoundException();
    }

}
