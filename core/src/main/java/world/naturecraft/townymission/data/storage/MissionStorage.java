package world.naturecraft.townymission.data.storage;

import world.naturecraft.naturelib.database.Storage;
import world.naturecraft.townymission.components.entity.MissionEntry;

import java.util.List;
import java.util.UUID;

/**
 * The interface Mission storage.
 */
public interface MissionStorage extends Storage<MissionEntry> {

    /**
     * Add.
     *
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the mission json
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player uuid
     * @param numMission        the slot where it is placed in the GUI
     */
    void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID, int numMission);

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
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the mission json
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player uuid
     */
    void update(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID, int numMission);

    List<MissionEntry> getEntries();
}
