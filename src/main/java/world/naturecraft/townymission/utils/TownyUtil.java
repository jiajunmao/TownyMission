/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.utils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

public class TownyUtil {

    public static Town mayorOf(Player player) {
        for(Town t : TownyAPI.getInstance().getDataSource().getTowns()) {
            if (t.hasMayor() && t.getMayor().getPlayer().equals(player)) {
                return t;
            }
        }
        return null;
    }

    public static Town residentOf(Player player) {
        for(Town t : TownyAPI.getInstance().getDataSource().getTowns()) {
            if (t.hasResident(player.getDisplayName())) {
                return t;
            }
        }

        return null;
    }
}