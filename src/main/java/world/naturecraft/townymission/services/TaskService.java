/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.dao.MissionDao;

public class TaskService {

    private MissionDao missionDao;
    private TownyMission instance;

    public TaskService(TownyMission instance) {
        this.instance = instance;
        this.missionDao = (MissionDao) instance.getDao(DbType.TASK);
    }

    public boolean hasStarted(Town town) {
        return missionDao.getStartedMission(town) == null;
    }

    public void startMission(Town town, int choice) {

    }
}
