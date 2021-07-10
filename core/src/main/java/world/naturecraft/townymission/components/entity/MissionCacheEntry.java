package world.naturecraft.townymission.components.entity;

import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;

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
