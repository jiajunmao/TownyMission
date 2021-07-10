package world.naturecraft.townymission.data.db;

import world.naturecraft.townymission.components.entity.SeasonHistoryEntry;

import java.util.List;
import java.util.UUID;

/**
 * The interface Season history storage.
 */
public interface SeasonHistoryStorage extends Storage<SeasonHistoryEntry> {

    /**
     * Add.
     *
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    void add(int season, long startedTime, String rankJson);

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
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    void update(UUID id, int season, long startedTime, String rankJson);

    List<SeasonHistoryEntry> getEntries();
}
