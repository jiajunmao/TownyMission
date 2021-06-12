/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.townymission.components.containers.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.data.db.SeasonHistoryStorage;

import java.util.List;

public class SeasonHistoryDao extends Dao<SeasonHistoryEntry> {

    private static SeasonHistoryDao singleton;
    private final SeasonHistoryStorage db;

    public SeasonHistoryDao() {
        this.db = SeasonHistoryStorage.getInstance();
    }

    public static SeasonHistoryDao getInstance() {
        if (singleton == null) {
            singleton = new SeasonHistoryDao();
        }

        return singleton;
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    @Override
    public List<SeasonHistoryEntry> getEntries() {
        return db.getEntries();
    }

    /**
     * Add.
     *
     * @param data the data
     */
    @Override
    public void add(SeasonHistoryEntry data) {
        db.add(data.getSeason(), data.getStartTime(), data.getRankJson());
    }

    /**
     * Update.
     *
     * @param data the data
     */
    @Override
    public void update(SeasonHistoryEntry data) {
        db.update(data.getId(), data.getSeason(), data.getStartTime(), data.getRankJson());
    }

    /**
     * Remove.
     *
     * @param data the data
     */
    @Override
    public void remove(SeasonHistoryEntry data) {
        db.remove(data.getId());
    }
}
