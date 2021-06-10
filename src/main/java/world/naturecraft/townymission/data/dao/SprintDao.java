/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.townymission.components.containers.sql.SprintEntry;
import world.naturecraft.townymission.data.db.sql.CooldownDatabase;
import world.naturecraft.townymission.data.db.sql.SprintDatabase;

import java.util.List;

/**
 * The type Sprint dao.
 */
public class SprintDao extends Dao<SprintEntry> {

    private static SprintDao singleton;
    private final SprintDatabase database;

    /**
     * Instantiates a new Sprint dao.
     *
     * @param database the database
     */
    public SprintDao(SprintDatabase database) {
        this.database = database;
    }

    public void update(SprintEntry entry) {
        if (get(entry.getTownID()) != null) {
            database.update(entry.getId(), entry.getTownID(), entry.getTownName(), entry.getNaturepoints(), entry.getSprint(), entry.getSeason());
        }
    }

    @Override
    public void remove(SprintEntry data) {
        database.remove(data.getId());
    }

    public void add(SprintEntry entry) {
        if (get(entry.getTownID()) == null) {
            database.add(entry.getTownID(), entry.getTownName(), entry.getNaturepoints(), entry.getSprint(), entry.getSeason());
        } else {
            update(entry);
        }
    }

    @Override
    public List<SprintEntry> getEntries() {
        return database.getEntries();
    }

    /**
     * Get sprint entry.
     *
     * @param townUUID the town uuid
     * @return the sprint entry
     */
    public SprintEntry get(String townUUID) {
        if (database.getEntries() == null)
            return null;

        for (SprintEntry s : database.getEntries()) {
            if (s.getTownID().equalsIgnoreCase(townUUID)) {
                System.out.println("SprintDao match with id: " + s.getId() + " and town name: " + s.getTownName());
                return s;
            }
        }
        return null;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintDao getInstance() {
        if (singleton == null) {
            singleton = new SprintDao(SprintDatabase.getInstance());
        }
        return singleton;
    }
}
