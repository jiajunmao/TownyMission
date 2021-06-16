/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.DataEntity;

import java.util.List;

/**
 * The type Storage.
 *
 * @param <T> the type parameter
 */
public abstract class Storage<T extends DataEntity> {

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public abstract List<T> getEntries();
}
