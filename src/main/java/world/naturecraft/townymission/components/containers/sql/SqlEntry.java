/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.sql;

import world.naturecraft.townymission.components.enums.DbType;

/**
 * The type Sql entry.
 */
public abstract class SqlEntry {
    private final DbType type;
    private int id;

    /**
     * Instantiates a new Sql entry.
     *
     * @param id   the id
     * @param type the type
     */
    public SqlEntry(int id, DbType type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }
}
