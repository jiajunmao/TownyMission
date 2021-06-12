/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.sql;

import world.naturecraft.townymission.components.enums.DbType;

import java.util.UUID;

/**
 * The type Sql entry.
 */
public abstract class SqlEntry {
    private final DbType type;
    private UUID id;

    /**
     * Instantiates a new Sql entry.
     *
     * @param id   the id
     * @param type the type
     */
    public SqlEntry(UUID id, DbType type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SqlEntry)) return false;

        SqlEntry entry = (SqlEntry) object;
        return id.equals(entry.getId());
    }
}
