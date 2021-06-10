/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.api.exceptions;

import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.components.containers.sql.MissionEntry;

public class NoStartedException extends RuntimeException {

    private MissionEntry entry;
    private Town town;

    public NoStartedException(MissionEntry entry) {
        this.entry = entry;
    }

    public NoStartedException(Town town) {
        this.town = town;
    }
}
