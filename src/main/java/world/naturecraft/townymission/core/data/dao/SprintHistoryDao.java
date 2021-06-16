/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.dao;

import world.naturecraft.townymission.core.components.entity.SprintHistoryEntry;
import world.naturecraft.townymission.core.data.db.SprintHistoryStorage;

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
        super(SprintHistoryStorage.getInstance());
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
     * Get sprint history entry.
     *
     * @param season the season
     * @param sprint the sprint
     * @return the sprint history entry
     */
    public SprintHistoryEntry get(int season, int sprint) {
        List<SprintHistoryEntry> entries = getEntries();
        for (SprintHistoryEntry e : entries) {
            if (e.getSprint() == sprint && e.getSeason() == season) {
                return e;
            }
        }

        return null;
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
