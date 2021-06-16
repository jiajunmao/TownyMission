package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.MissionHistoryEntry;

import java.util.UUID;

public interface MissionHistoryStorage extends Storage<MissionHistoryEntry> {

    void add(String missionType, long addedTime, long startedTime, long allowedTime, String taskJson, String townName, String startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season);

    void remove(UUID id);

    void update(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String taskJson, String townName, String startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season);
}
