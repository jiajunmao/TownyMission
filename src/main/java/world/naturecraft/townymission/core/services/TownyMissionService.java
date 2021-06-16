/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.services;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;

/**
 * The type Towny mission service.
 */
public abstract class TownyMissionService {

    /**
     * The Instance.
     */
    protected TownyMission instance;

    /**
     * Instantiates a new Towny mission service.
     */
    public TownyMissionService() {
        this.instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
    }
}
