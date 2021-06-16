package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.SeasonEntry;
import world.naturecraft.townymission.core.services.StorageService;

import java.util.List;
import java.util.UUID;

public interface SeasonStorage extends Storage<SeasonEntry> {

    void add(String townUUID, String townName, int seasonPoint, int season);

    void remove(UUID id);

    void update(UUID id, String townUUID, String townName, int seasonPoint, int season);

    List<SeasonEntry> getEntries();
}
