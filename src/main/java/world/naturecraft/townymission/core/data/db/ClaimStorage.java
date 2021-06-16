package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.ClaimEntry;

import java.util.UUID;

public interface ClaimStorage extends Storage<ClaimEntry> {

    void add(String playerUUID, String rewardType, String rewardJson, int season, int sprint);

    void update(UUID uuid, String playerUUID, String rewardType, String rewardJson, int season, int sprint);

    void remove(UUID id);
}
