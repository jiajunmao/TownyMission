/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.api.exceptions.DataProcessException;
import world.naturecraft.townymission.components.containers.sql.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.sql.MissionDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Task dao.
 */
public class MissionDao extends Dao<MissionEntry> {

    private static MissionDao singleton;
    private final MissionDatabase db;

    /**
     * Instantiates a new Task dao.
     */
    public MissionDao() {
        this.db = MissionDatabase.getInstance();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionDao getInstance() {
        if (singleton == null) {
            singleton = new MissionDao();
        }
        return singleton;
    }

    /**
     * Gets num added.
     *
     * @param town the town
     * @return the num added
     */
    public int getNumAdded(Town town) {
        int num = 0;
        for (MissionEntry e : db.getEntries()) {
            if (e.getTown().equals(town)) {
                num++;
            }
        }

        return num;
    }

    /**
     * Gets town tasks.
     *
     * @param town the town
     * @return the town tasks
     */
    public List<MissionEntry> getTownMissions(Town town) {
        List<MissionEntry> list = new ArrayList<>();
        for (MissionEntry e : db.getEntries()) {
            if (e.getTown().equals(town)) {
                list.add(e);
            }
        }

        return list;
    }

    /**
     * Gets town tasks.
     *
     * @param town        the town
     * @param missionType the mission type
     * @return the town tasks
     */
    @Deprecated
    public MissionEntry getTownStartedMission(Town town, MissionType missionType) {
        List<MissionEntry> list = getTownMissions(town);

        for (MissionEntry e : list) {
            if (e.getMissionType().equals(missionType) && e.getStartedTime() != 0) {
                return e;
            }
        }

        return null;
    }

    /**
     * Gets started mission.
     *
     * @param town the town
     * @return the started mission
     */
    @Deprecated
    public MissionEntry getStartedMission(Town town) {
        for (MissionEntry e : db.getEntries()) {
            if (e.getTown().equals(town)) {
                if (e.getStartedTime() != 0) {
                    return e;
                }
            }
        }

        return null;
    }

    /**
     * Add.
     *
     * @param entry the entry
     * @throws DataProcessException the json processing exception
     */
    public void add(MissionEntry entry) {
        try {
            db.add(entry.getMissionType().name(), entry.getAddedTime(), entry.getStartedTime(), entry.getAllowedTime(), entry.getMissionJson().toJson(), entry.getTown().getName(), entry.getStartedPlayer() == null ? null : entry.getStartedPlayer().getUniqueId().toString());
        } catch (JsonProcessingException e) {
            throw new DataProcessException(e);
        }
    }

    /**
     * Remove.
     *
     * @param entry the entry
     */
    public void remove(MissionEntry entry) {
        db.remove(entry.getId());
    }

    /**
     * Update.
     *
     * @param entry the entry
     * @throws DataProcessException the json processing exception
     */
    public void update(MissionEntry entry) {
        try {
            db.update(entry.getId(), entry.getMissionType().name(), entry.getAddedTime(), entry.getStartedTime(), entry.getAllowedTime(), entry.getMissionJson().toJson(), entry.getTown().getName(), entry.getStartedPlayer().getUniqueId().toString());
        } catch (JsonProcessingException e) {
            throw new DataProcessException(e);
        }
    }

    @Override
    public List<MissionEntry> getEntries() {
        return db.getEntries();
    }
}
