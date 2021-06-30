package world.naturecraft.townymission.core.components.entity;

import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.MissionJson;

import java.util.UUID;

public class MissionCacheEntry extends DataEntity {

    private UUID playerUUID;
    private MissionType missionType;
    private int amount;

    public MissionCacheEntry(UUID uuid, UUID playerUUID, MissionType missionType, int amount) {
        super(uuid, DbType.MISSION_CACHE);
        this.playerUUID = playerUUID;
        this.missionType = missionType;
        this.amount = amount;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public int getAmount() {
        return amount;
    }
}
