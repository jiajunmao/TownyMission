/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.services;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;

/**
 * The type Towny mission service.
 */
public abstract class TownyMissionService {

    /**
     * The Instance.
     */
    protected TownyMissionInstance instance;

    /**
     * Instantiates a new Towny mission service.
     */
    public TownyMissionService(TownyMissionInstance instance) {
        this.instance = instance;
    }
}
