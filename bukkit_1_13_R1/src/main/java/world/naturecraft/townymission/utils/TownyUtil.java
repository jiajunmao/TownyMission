/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.utils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

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
    public static Town mayorOf(OfflinePlayer player) {
        for (Town t : TownyAPI.getInstance().getDataSource().getTowns()) {
            if (t.hasMayor() && t.getMayor() != null && t.getMayor().getPlayer() != null && t.getMayor().getPlayer().equals(player)) {
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
    public static Town residentOf(OfflinePlayer player) {
        if (player == null)
            return null;

        try {
            for (Resident r : TownyAPI.getInstance().getDataSource().getResidents()) {
                if (r.getUUID().equals(player.getUniqueId()) && r.hasTown()) {
                    return r.getTown();
                }
            }

            return null;
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    /**
     * Gets town.
     *
     * @param townUUID the town uuid
     * @return the town
     */
    public static Town getTown(UUID townUUID) {
        try {
            return TownyAPI.getInstance().getDataSource().getTown(townUUID);
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public static Town getTown(String townName) {
        try {
            return TownyAPI.getInstance().getDataSource().getTown(townName);
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public static List<Town> getTowns() {
        return TownyAPI.getInstance().getDataSource().getTowns();
    }
}
