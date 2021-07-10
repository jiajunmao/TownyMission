package world.naturecraft.townymission.data.db;

import world.naturecraft.townymission.components.entity.SeasonEntry;

import java.util.List;
import java.util.UUID;

/**
 * The interface Season storage.
 */
public interface SeasonStorage extends Storage<SeasonEntry> {

    /**
     * Add.
     *
     * @param townUUID    the town uuid
     * @param seasonPoint the season point
     * @param season      the season
     */
    void add(UUID townUUID, int seasonPoint, int season);

    /**
     * Remove.
     *
     * @param id the id
     */
    void remove(UUID id);

    /**
     * Update.
     *
     * @param id          the id
     * @param townUUID    the town uuid
     * @param seasonPoint the season point
     * @param season      the season
     */
    void update(UUID id, UUID townUUID, int seasonPoint, int season);

    List<SeasonEntry> getEntries();
}
