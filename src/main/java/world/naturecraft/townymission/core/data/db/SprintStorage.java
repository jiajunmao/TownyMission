package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.SprintEntry;

import java.util.List;
import java.util.UUID;

public interface SprintStorage extends Storage<SprintEntry> {

    void add(String townUUID, String townName, int naturePoints, int sprint, int season);

    void remove(UUID id);

    void update(UUID id, String townUUID, String townName, int naturePoints, int sprint, int season);

    List<SprintEntry> getEntries();
}
