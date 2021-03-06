/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.naturelib.database.Dao;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.CooldownStorage;
import world.naturecraft.townymission.services.core.StorageService;

import java.util.List;
import java.util.UUID;

/**
 * The type Cooldown dao.
 */
public class CooldownDao extends Dao<CooldownEntry> {

    private static CooldownDao singleton;
    private final CooldownStorage db;

    /**
     * Instantiates a new Cooldown dao.
     */
    public CooldownDao() {
        super(StorageService.getInstance().getStorage(DbType.COOLDOWN));
        this.db = StorageService.getInstance().getStorage(DbType.COOLDOWN);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CooldownDao getInstance() {
        if (singleton == null) {
            singleton = new CooldownDao();
        }
        return singleton;
    }

    @Override
    public void add(CooldownEntry data) {
        db.add(data.getTownUUID(), data.getCooldownJsonList().toJson());
    }

    @Override
    public void update(CooldownEntry data) {
        db.update(data.getId(), data.getTownUUID(), data.getCooldownJsonList().toJson());
    }

    @Override
    public void remove(CooldownEntry data) {
        db.remove(data.getId());
    }

    /**
     * Get cooldown entry.
     *
     * @param townUUID the town
     * @return the cooldown entry
     */
    public CooldownEntry get(UUID townUUID) {
        List<CooldownEntry> list = getEntries(data -> (data.getTownUUID().equals(townUUID)));

        if (list.size() != 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Remove all entries.
     */
    public void removeAllEntries() {
        List<CooldownEntry> entries = getEntries();
        for (CooldownEntry entry : entries) {
            remove(entry);
        }
    }
}
