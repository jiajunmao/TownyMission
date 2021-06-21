package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.SprintHistoryEntry;

import java.util.List;
import java.util.UUID;

/**
 * The interface Sprint history storage.
 */
public interface SprintHistoryStorage extends Storage<SprintHistoryEntry> {

    /**
     * Add.
     *
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    void add(int season, int sprint, long startedTime, String rankJson);

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
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    void update(UUID id, int season, int sprint, long startedTime, String rankJson);

    List<SprintHistoryEntry> getEntries();
}
