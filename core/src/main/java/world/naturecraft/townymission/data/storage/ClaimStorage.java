package world.naturecraft.townymission.data.storage;

import world.naturecraft.townymission.components.entity.ClaimEntry;

import java.util.UUID;

/**
 * The interface Claim storage.
 */
public interface ClaimStorage extends Storage<ClaimEntry> {

    /**
     * Add.
     *
     * @param playerUUID the player uuid
     * @param rewardType the reward type
     * @param rewardJson the reward json
     * @param season     the season
     * @param sprint     the sprint
     */
    void add(UUID playerUUID, String rewardType, String rewardJson, int season, int sprint);

    /**
     * Update.
     *
     * @param uuid       the uuid
     * @param playerUUID the player uuid
     * @param rewardType the reward type
     * @param rewardJson the reward json
     * @param season     the season
     * @param sprint     the sprint
     */
    void update(UUID uuid, UUID playerUUID, String rewardType, String rewardJson, int season, int sprint);

    /**
     * Remove.
     *
     * @param id the id
     */
    void remove(UUID id);
}
