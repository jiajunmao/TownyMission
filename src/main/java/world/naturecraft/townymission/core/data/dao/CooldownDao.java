/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.dao;

import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.core.components.entity.CooldownEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.data.db.CooldownStorage;
import world.naturecraft.townymission.core.services.StorageService;
import world.naturecraft.townymission.core.utils.EntryFilter;

import java.util.List;

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
        db.add(data.getTown().getUUID().toString(), data.getCooldownsAsString());
    }

    @Override
    public void update(CooldownEntry data) {
        db.update(data.getId(), data.getTown().getUUID().toString(), data.getCooldownsAsString());
    }

    @Override
    public void remove(CooldownEntry data) {
        db.remove(data.getId());
    }

    /**
     * Get cooldown entry.
     *
     * @param town the town
     * @return the cooldown entry
     */
    public CooldownEntry get(Town town) {
        List<CooldownEntry> list = getEntries(new EntryFilter<CooldownEntry>() {
            @Override
            public boolean include(CooldownEntry data) {
                return (data.getTown().equals(town));
            }
        });

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
