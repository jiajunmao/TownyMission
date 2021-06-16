/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.dao;

import world.naturecraft.townymission.core.components.entity.DataEntity;
import world.naturecraft.townymission.core.data.db.Storage;
import world.naturecraft.townymission.bukkit.utils.EntryFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Dao.
 *
 * @param <T> the type parameter
 */
public abstract class Dao<T extends DataEntity> {

    private final Storage<T> storage;

    /**
     * Instantiates a new Dao.
     *
     * @param storage the storage
     */
    public Dao(Storage<T> storage) {
        this.storage = storage;
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<T> getEntries() {
        return storage.getEntries();
    }

    /**
     * Gets entries.
     *
     * @param filter the filter
     * @return the entries
     */
    public List<T> getEntries(EntryFilter<T> filter) {
        List<T> list = getEntries();
        List<T> finalList = new ArrayList<>();
        for (T entry : list) {
            if (filter.include(entry)) {
                finalList.add(entry);
            }
        }

        return finalList;
    }

    /**
     * Add.
     *
     * @param data the data
     */
    public abstract void add(T data);

    /**
     * Update.
     *
     * @param data the data
     */
    public abstract void update(T data);

    /**
     * Remove.
     *
     * @param data the data
     */
    public abstract void remove(T data);
}
