package world.naturecraft.townymission.data.db;

import world.naturecraft.townymission.components.entity.MissionHistoryEntry;

import java.util.UUID;

/**
 * The interface Mission history storage.
 */
public interface MissionHistoryStorage extends Storage<MissionHistoryEntry> {

    /**
     * Add.
     *
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param taskJson          the task json
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player uuid
     * @param completedTime     the completed time
     * @param isClaimed         the is claimed
     * @param sprint            the sprint
     * @param season            the season
     */
    void add(String missionType, long addedTime, long startedTime, long allowedTime, String taskJson, UUID townUUID, UUID startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season);

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
     * @param taskJson          the task json
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player uuid
     * @param completedTime     the completed time
     * @param isClaimed         the is claimed
     * @param sprint            the sprint
     * @param season            the season
     */
    void update(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String taskJson, UUID townUUID, UUID startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season);
}
