package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.core.services.StorageService;

import java.util.List;
import java.util.UUID;

public interface SeasonHistoryStorage extends Storage<SeasonHistoryEntry> {

    void add(int season, long startedTime, String rankJson);

    void remove(UUID id);

    void update(UUID id, int season, long startedTime, String rankJson);

    List<SeasonHistoryEntry> getEntries();
}
