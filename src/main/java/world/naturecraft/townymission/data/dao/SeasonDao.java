/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.data.db.SeasonStorage;
import world.naturecraft.townymission.data.sql.SeasonDatabase;

import java.util.List;

/**
 * The type Season dao.
 */
public class SeasonDao extends Dao<SeasonEntry> {

    private static SeasonDao singleton;
    private final SeasonStorage database;

    /**
     * Instantiates a new Season dao.
     */
    public SeasonDao() {
        this.database = SeasonStorage.getInstance();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonDao getInstance() {
        if (singleton == null) {
            singleton = new SeasonDao();
        }
        return singleton;
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

    /**
     * Get season entry.
     *
     * @param townUUID the town uuid
     * @return the season entry
     */
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
