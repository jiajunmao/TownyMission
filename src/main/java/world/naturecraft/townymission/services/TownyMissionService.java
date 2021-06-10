/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.dao.*;

public abstract class TownyMissionService {

    protected TownyMission instance;

    public TownyMissionService(TownyMission instance) {
        this.instance = instance;
    }
}
