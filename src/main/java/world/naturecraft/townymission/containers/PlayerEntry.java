package world.naturecraft.townymission.containers;

import java.util.UUID;

public class PlayerEntry {
    private final int id;
    private final String uuid;
    private final String displayName;

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
}
