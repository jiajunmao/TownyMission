/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import world.naturecraft.townymission.components.containers.sql.SqlEntry;

import java.util.List;

/**
 * The type Dao.
 *
 * @param <T> the type parameter
 */
public abstract class Dao<T extends SqlEntry> {

    public abstract List<T> getEntries();

    public abstract void add(T data);

    public abstract void update(T data);

    public abstract void remove(T data);
}
