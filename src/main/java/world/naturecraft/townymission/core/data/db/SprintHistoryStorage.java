package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.SprintHistoryEntry;

import java.util.List;
import java.util.UUID;

public interface SprintHistoryStorage extends Storage<SprintHistoryEntry> {

    void add(int season, int sprint, long startedTime, String rankJson);

    void remove(UUID id);

    void update(UUID id, int season, int sprint, long startedTime, String rankJson);

    List<SprintHistoryEntry> getEntries();
}
