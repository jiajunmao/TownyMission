/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.dao.*;

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
     *
     * @param instance the instance
     */
    public TownyMissionService(TownyMission instance) {
        this.instance = instance;
    }
}
