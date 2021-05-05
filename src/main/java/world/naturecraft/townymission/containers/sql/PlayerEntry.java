/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.containers.sql;

public class PlayerEntry {
    private int id;
    private String uuid;
    private String displayName;

    public PlayerEntry(int id, String uuid, String displayName) {
        this.id = id;
        this.uuid = uuid;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
