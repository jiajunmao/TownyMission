package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.CooldownEntry;

import java.util.UUID;

public interface CooldownStorage extends Storage<CooldownEntry> {

    void add(String townUUID, String cooldownJsonList);

    void remove(UUID id);

    void update(UUID id, String townUUID, String cooldownJsonList);
}
