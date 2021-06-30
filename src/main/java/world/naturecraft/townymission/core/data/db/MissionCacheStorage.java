package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.MissionJson;

import java.util.List;
import java.util.UUID;

public interface MissionCacheStorage extends Storage<MissionCacheEntry> {

    /**
     * Add.
     *
     * @param missionType       the mission type
     */
    void add(UUID playerUUID, MissionType missionType, int amount);

    /**
     * Remove.
     *
     * @param id the id
     */
    void remove(UUID id);

    /**
     * Update.
     *
     * @param id                the id
     * @param missionType       the mission type
     */
    void update(UUID id, UUID playerUUID, MissionType missionType, int amount);

    List<MissionCacheEntry> getEntries();
}
