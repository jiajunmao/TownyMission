/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.api.exceptions;

import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.core.components.entity.MissionEntry;

/**
 * The type No started exception.
 */
public class NoStartedException extends RuntimeException {

    private MissionEntry entry;
    private Town town;

    /**
     * Instantiates a new No started exception.
     *
     * @param entry the entry
     */
    public NoStartedException(MissionEntry entry) {
        this.entry = entry;
    }

    /**
     * Instantiates a new No started exception.
     *
     * @param town the town
     */
    public NoStartedException(Town town) {
        this.town = town;
    }
}
