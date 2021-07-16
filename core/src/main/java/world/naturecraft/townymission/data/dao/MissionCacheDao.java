/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.townymission.api.exceptions.DataProcessException;
import world.naturecraft.townymission.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.MissionCacheStorage;
import world.naturecraft.townymission.services.StorageService;

/**
 * The type Task dao.
 */
public class MissionCacheDao extends Dao<MissionCacheEntry> {

    private static MissionCacheDao singleton;
    private final MissionCacheStorage db;

    /**
     * Instantiates a new Task dao.
     */
    public MissionCacheDao() {
        super(StorageService.getInstance().getStorage(DbType.MISSION_CACHE));
        this.db = StorageService.getInstance().getStorage(DbType.MISSION_CACHE);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionCacheDao getInstance() {
        if (singleton == null) {
            singleton = new MissionCacheDao();
        }
        return singleton;
    }

    /**
     * Add.
     *
     * @param entry the entry
     * @throws DataProcessException the json processing exception
     */
    public void add(MissionCacheEntry entry) {
        db.add(entry.getPlayerUUID(),
                entry.getMissionType(),
                entry.getAmount());
    }

    /**
     * Remove.
     *
     * @param entry the entry
     */
    public void remove(MissionCacheEntry entry) {
        db.remove(entry.getId());
    }

    /**
     * Update.
     *
     * @param entry the entry
     * @throws DataProcessException the json processing exception
     */
    public void update(MissionCacheEntry entry) {
        db.update(
                entry.getId(),
                entry.getPlayerUUID(),
                entry.getMissionType(),
                entry.getAmount());
    }

    @Override
    public void reloadDb() {
        singleton = new MissionCacheDao();
    }
}
