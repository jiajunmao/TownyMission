/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.utils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

/**
 * The type Towny util.
 */
public class TownyUtil {

    /**
     * Mayor of town.
     *
     * @param player the player
     * @return the town
     */
    public static Town mayorOf(Player player) {
        for (Town t : TownyAPI.getInstance().getDataSource().getTowns()) {
            if (t.hasMayor() && t.getMayor().getPlayer().equals(player)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Resident of town.
     *
     * @param player the player
     * @return the town
     */
    public static Town residentOf(Player player) {
        for (Town t : TownyAPI.getInstance().getDataSource().getTowns()) {
            if (t.hasResident(player.getDisplayName())) {
                return t;
            }
        }

        return null;
    }

    public static Town getTownByName(String townName) {
        try {
            return TownyAPI.getInstance().getDataSource().getTown(townName);
        } catch (NotRegisteredException exception) {
            return null;
        }
    }
}
