/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.dao.*;

public abstract class TownyMissionService {

    protected MissionDao missionDao;
    protected MissionHistoryDao missionHistoryDao;
    protected CooldownDao cooldownDao;
    protected SprintDao sprintDao;
    protected SeasonDao seasonDao;
    protected TownyMission instance;

    public TownyMissionService(TownyMission instance) {
        this.instance = instance;
        this.missionDao = (MissionDao) instance.getDao(DbType.MISSION);
        this.missionHistoryDao = (MissionHistoryDao) instance.getDao(DbType.MISSION_HISTORY);
        this.cooldownDao = (CooldownDao) instance.getDao(DbType.COOLDOWN);
        this.sprintDao = (SprintDao) instance.getDao(DbType.SPRINT);
        this.seasonDao = (SeasonDao) instance.getDao(DbType.SEASON);
    }
}
