/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.utils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
     * @throws NotRegisteredException the not registered exception
     */
    public static Town getTown(UUID townUUID) throws NotRegisteredException {
        return TownyAPI.getInstance().getDataSource().getTown(townUUID);
    }
}
