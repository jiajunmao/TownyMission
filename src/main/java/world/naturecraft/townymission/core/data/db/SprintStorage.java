package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.SprintEntry;

import java.util.List;
import java.util.UUID;

/**
 * The interface Sprint storage.
 */
public interface SprintStorage extends Storage<SprintEntry> {

    /**
     * Add.
     *
     * @param townUUID     the town uuid
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    void add(UUID townUUID, int naturePoints, int sprint, int season);

    /**
     * Remove.
     *
     * @param id the id
     */
    void remove(UUID id);

    /**
     * Update.
     *
     * @param id           the id
     * @param townUUID     the town uuid
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    void update(UUID id, UUID townUUID, int naturePoints, int sprint, int season);

    List<SprintEntry> getEntries();
}
