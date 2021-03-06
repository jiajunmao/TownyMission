/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.naturelib.database.Dao;
import world.naturecraft.townymission.components.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.SeasonHistoryStorage;
import world.naturecraft.townymission.services.core.StorageService;

import java.util.List;

/**
 * The type Season history dao.
 */
public class SeasonHistoryDao extends Dao<SeasonHistoryEntry> {

    private static SeasonHistoryDao singleton;
    private final SeasonHistoryStorage db;

    /**
     * Instantiates a new Season history dao.
     */
    public SeasonHistoryDao() {
        super(StorageService.getInstance().getStorage(DbType.SEASON_HISTORY));
        this.db = StorageService.getInstance().getStorage(DbType.SEASON_HISTORY);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonHistoryDao getInstance() {
        if (singleton == null) {
            singleton = new SeasonHistoryDao();
        }

        return singleton;
    }

    /**
     * Get season history entry.
     *
     * @param season the season
     * @return the season history entry
     */
    public SeasonHistoryEntry get(int season) {
        List<SeasonHistoryEntry> entries = getEntries();
        for (SeasonHistoryEntry e : entries) {
            if (e.getSeason() == season) {
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
