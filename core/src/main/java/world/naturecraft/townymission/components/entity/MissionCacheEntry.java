package world.naturecraft.townymission.components.entity;

import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;

import java.util.Date;
import java.util.UUID;

public class MissionCacheEntry extends DataEntity {

    private UUID playerUUID;
    private MissionType missionType;
    private int amount;
    private long lastAttempted;
    private int retryCount;

    public MissionCacheEntry(UUID uuid, UUID playerUUID, MissionType missionType, int amount, long lastAttempted, int retryCount) {
        super(uuid, DbType.MISSION_CACHE);
        this.playerUUID = playerUUID;
        this.missionType = missionType;
        this.amount = amount;
        this.lastAttempted = lastAttempted;
        this.retryCount = retryCount;
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

    public long getLastAttempted() {
        return lastAttempted;
    }

    public void setLastAttempted(long lastAttempted) {
        this.lastAttempted = lastAttempted;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void incrementFail() {
        this.retryCount++;
        this.lastAttempted = new Date().getTime();
    }
}
