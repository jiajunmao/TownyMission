package world.naturecraft.townymission.data.storage;

import world.naturecraft.naturelib.database.Storage;
import world.naturecraft.townymission.components.entity.CooldownEntry;

import java.util.UUID;

/**
 * The interface Cooldown storage.
 */
public interface CooldownStorage extends Storage<CooldownEntry> {

    /**
     * Add.
     *
     * @param townUUID         the town uuid
     * @param cooldownJsonList the cooldown json list
     */
    void add(UUID townUUID, String cooldownJsonList);

    /**
     * Remove.
     *
     * @param id the id
     */
    void remove(UUID id);

    /**
     * Update.
     *
     * @param id               the id
     * @param townUUID         the town uuid
     * @param cooldownJsonList the cooldown json list
     */
    void update(UUID id, UUID townUUID, String cooldownJsonList);
}
