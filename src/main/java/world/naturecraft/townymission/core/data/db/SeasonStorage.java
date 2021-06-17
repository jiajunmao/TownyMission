package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.SeasonEntry;
import world.naturecraft.townymission.core.services.StorageService;

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
     * @param townName    the town name
     * @param seasonPoint the season point
     * @param season      the season
     */
    void add(String townUUID, String townName, int seasonPoint, int season);

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
     * @param townName    the town name
     * @param seasonPoint the season point
     * @param season      the season
     */
    void update(UUID id, String townUUID, String townName, int seasonPoint, int season);

    List<SeasonEntry> getEntries();
}
