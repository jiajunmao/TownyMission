/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.components.entity;

import world.naturecraft.townymission.core.components.enums.DbType;

import java.util.UUID;

/**
 * The type Sql entry.
 */
public abstract class DataEntity {
    private final DbType type;
    private UUID id;

    /**
     * Instantiates a new Sql entry.
     *
     * @param id   the id
     * @param type the type
     */
    public DataEntity(UUID id, DbType type) {
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
        if (!(object instanceof DataEntity)) return false;

        DataEntity entry = (DataEntity) object;
        return id.equals(entry.getId());
    }
}
