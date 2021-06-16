package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.MissionEntry;

import java.util.List;
import java.util.UUID;

public interface MissionStorage extends Storage<MissionEntry> {

    void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID);

    void remove(UUID id);

    void update(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID);

    List<MissionEntry> getEntries();
}
