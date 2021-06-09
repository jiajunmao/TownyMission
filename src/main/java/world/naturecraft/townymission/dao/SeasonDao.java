/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.dao;

import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.components.containers.sql.SprintEntry;
import world.naturecraft.townymission.db.sql.SeasonDatabase;
import world.naturecraft.townymission.db.sql.SprintDatabase;

import java.util.List;

public class SeasonDao extends Dao<SeasonEntry> {
    private final SeasonDatabase database;

    public SeasonDao(SeasonDatabase database) {
        this.database = database;
    }

    public void update(SeasonEntry entry) {
        if (get(entry.getTownID()) != null) {
            database.update(entry.getId(), entry.getTownID(), entry.getTownName(), entry.getSeasonPoint(), entry.getSeason());
        }
    }

    @Override
    public void remove(SeasonEntry data) {
        database.remove(data.getId());
    }

    public void add(SeasonEntry entry) {
        if (get(entry.getTownID()) == null) {
            database.add(entry.getTownID(), entry.getTownName(), entry.getSeasonPoint(), entry.getSeason());
        } else {
            update(entry);
        }
    }

    @Override
    public List<SeasonEntry> getEntries() {
        return database.getEntries();
    }

    public SeasonEntry get(String townUUID) {
        if (database.getEntries() == null)
            return null;

        for (SeasonEntry s : database.getEntries()) {
            if (s.getTownID().equalsIgnoreCase(townUUID)) {
                return s;
            }
        }
        return null;
    }
}
