/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.townymission.components.containers.entity.SprintHistoryEntry;
import world.naturecraft.townymission.data.db.SprintHistoryStorage;

import java.util.List;

/**
 * The type Sprint history dao.
 */
public class SprintHistoryDao extends Dao<SprintHistoryEntry> {

    private static SprintHistoryDao singleton;
    private final SprintHistoryStorage db;

    /**
     * Instantiates a new Sprint history dao.
     */
    public SprintHistoryDao() {
        this.db = SprintHistoryStorage.getInstance();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintHistoryDao getInstance() {
        if (singleton == null) {
            singleton = new SprintHistoryDao();
        }

        return singleton;
    }


    /**
     * Gets entries.
     *
     * @return the entries
     */
    @Override
    public List<SprintHistoryEntry> getEntries() {
        return db.getEntries();
    }

    /**
     * Add.
     *
     * @param data the data
     */
    @Override
    public void add(SprintHistoryEntry data) {
        db.add(data.getSeason(), data.getSprint(), data.getStartedTime(), data.getRankJson());
    }

    /**
     * Update.
     *
     * @param data the data
     */
    @Override
    public void update(SprintHistoryEntry data) {
        db.update(data.getId(), data.getSeason(), data.getSprint(), data.getStartedTime(), data.getRankJson());
    }

    /**
     * Remove.
     *
     * @param data the data
     */
    @Override
    public void remove(SprintHistoryEntry data) {
        db.remove(data.getId());
    }
}
