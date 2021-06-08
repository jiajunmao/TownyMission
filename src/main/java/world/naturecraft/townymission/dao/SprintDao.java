/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.dao;

import world.naturecraft.townymission.components.containers.sql.SprintEntry;
import world.naturecraft.townymission.db.sql.SprintDatabase;

public class SprintDao extends Dao<SprintEntry> {

    private final SprintDatabase database;

    public SprintDao(SprintDatabase database) {
        this.database = database;
    }

    public void update(SprintEntry entry) {
        if (get(entry.getTownID()) != null) {
            System.out.println("Updating sprint entry for town: " + entry.getTownName() + " and naturepoints: " + entry.getNaturepoints() + " with id: " + entry.getId());
            database.update(entry.getId(), entry.getTownID(), entry.getTownName(), entry.getNaturepoints(), entry.getSprint(), entry.getSeason());
        }
    }

    public void add(SprintEntry entry) {
        if (get(entry.getTownID()) == null) {
            database.add(entry.getTownID(), entry.getTownName(), entry.getNaturepoints(), entry.getSprint(), entry.getSeason());
        } else {
            update(entry);
        }
    }

    public SprintEntry get(String townUUID) {
        for (SprintEntry s : database.getEntries()) {
            if (s.getTownID().equalsIgnoreCase(townUUID)) {
                System.out.println("SprintDao match with id: " + s.getId() + " and town name: " + s.getTownName());
                return s;
            }
        }
        return null;
    }
}
