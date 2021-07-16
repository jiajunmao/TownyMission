package world.naturecraft.townymission.components.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.exceptions.ConfigParsingException;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.json.mission.MissionJson;
import world.naturecraft.townymission.utils.MissionJsonFactory;

import java.util.UUID;

/**
 * The type Task history entry.
 */
public class MissionHistoryEntry extends DataEntity {

    private MissionType missionType;
    private long addedTime;
    private long startedTime;
    private long allowedTime;
    private MissionJson missionJson;
    private UUID townUUID;
    private UUID startedPlayerUUID;
    private long completedTime;
    private boolean claimed;
    private int sprint;
    private int season;

    /**
     * Instantiates a new Task history entry.
     *
     * @param id                the id
     * @param missionType       the task type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the task json
     * @param townUUID          the town
     * @param startedPlayerUUID the started player
     * @param completedTime     the completed time
     * @param claimed           the claimed
     * @param sprint            the sprint
     * @param season            the season
     */
    public MissionHistoryEntry(UUID id, MissionType missionType, long addedTime, long startedTime, long allowedTime,
                               MissionJson missionJson, UUID townUUID, UUID startedPlayerUUID, long completedTime, boolean claimed,
                               int sprint, int season) {
        super(id, DbType.MISSION_HISTORY);
        this.missionType = missionType;
        this.addedTime = addedTime;
        this.startedTime = startedTime;
        this.allowedTime = allowedTime;
        this.missionJson = missionJson;
        this.townUUID = townUUID;
        this.startedPlayerUUID = startedPlayerUUID;
        this.completedTime = completedTime;
        this.claimed = claimed;
        this.sprint = sprint;
        this.season = season;
    }

    /**
     * Instantiates a new Mission history entry.
     *
     * @param id                the id
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the mission json
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player
     * @param completedTime     the completed time
     * @param claimed           the claimed
     * @param sprint            the sprint
     * @param season            the season
     * @throws ConfigParsingException the json processing exception
     */
    public MissionHistoryEntry(UUID id, String missionType, long addedTime, long startedTime, long allowedTime,
                               String missionJson, UUID townUUID, UUID startedPlayerUUID, long completedTime,
                               boolean claimed, int sprint, int season) throws ConfigParsingException {
        this(id, MissionType.valueOf(missionType), addedTime, startedTime, allowedTime, null, townUUID, startedPlayerUUID, completedTime, claimed, sprint, season);
        try {
            this.missionJson = MissionJsonFactory.getJson(missionJson, MissionType.valueOf(missionType));
        } catch (JsonProcessingException e) {
            throw new ConfigParsingException(e);
        }
    }

    /**
     * Instantiates a new Mission history entry.
     *
     * @param entry         the entry
     * @param completedTime the completed time
     */
    public MissionHistoryEntry(MissionEntry entry, long completedTime) {
        this(UUID.randomUUID(), entry.getMissionType(), entry.getAddedTime(), entry.getStartedTime(), entry.getAllowedTime(),
                entry.getMissionJson(), entry.getTownUUID(), entry.getStartedPlayerUUID(), completedTime, false, 0, 0);
        TownyMissionInstance instance = TownyMissionInstance.getInstance();
        setSprint(instance.getStatsConfig().getInt("sprint.current"));
        setSeason(instance.getStatsConfig().getInt("season.current"));
    }

    /**
     * Gets task type.
     *
     * @return the task type
     */
    public MissionType getMissionType() {
        return missionType;
    }

    /**
     * Sets task type.
     *
     * @param missionType the task type
     */
    public void setMissionType(MissionType missionType) {
        this.missionType = missionType;
    }

    /**
     * Gets started time.
     *
     * @return the started time
     */
    public long getStartedTime() {
        return startedTime;
    }

    /**
     * Sets started time.
     *
     * @param startedTime the started time
     */
    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

    /**
     * Gets allowed time.
     *
     * @return the allowed time
     */
    public long getAllowedTime() {
        return allowedTime;
    }

    /**
     * Sets allowed time.
     *
     * @param allowedTime the allowed time
     */
    public void setAllowedTime(long allowedTime) {
        this.allowedTime = allowedTime;
    }

    /**
     * Gets task json.
     *
     * @return the task json
     */
    public MissionJson getMissionJson() {
        return missionJson;
    }

    /**
     * Sets mission json.
     *
     * @param missionJson the mission json
     */
    public void setMissionJson(MissionJson missionJson) {
        this.missionJson = missionJson;
    }

    /**
     * Sets task json.
     *
     * @param missionJson the task json
     */
    public void setTaskJson(MissionJson missionJson) {
        this.missionJson = missionJson;
    }


    /**
     * Gets town uuid.
     *
     * @return the town uuid
     */
    public UUID getTownUUID() {
        return townUUID;
    }

    /**
     * Sets town uuid.
     *
     * @param townUUID the town uuid
     */
    public void setTownUUID(UUID townUUID) {
        this.townUUID = townUUID;
    }

    /**
     * Gets completed time.
     *
     * @return the completed time
     */
    public long getCompletedTime() {
        return completedTime;
    }

    /**
     * Sets completed time.
     *
     * @param completedTime the completed time
     */
    public void setCompletedTime(long completedTime) {
        this.completedTime = completedTime;
    }

    /**
     * Gets sprint.
     *
     * @return the sprint
     */
    public int getSprint() {
        return sprint;
    }

    /**
     * Sets sprint.
     *
     * @param sprint the sprint
     */
    public void setSprint(int sprint) {
        this.sprint = sprint;
    }

    /**
     * Gets season.
     *
     * @return the season
     */
    public int getSeason() {
        return season;
    }

    /**
     * Sets season.
     *
     * @param season the season
     */
    public void setSeason(int season) {
        this.season = season;
    }

    /**
     * Gets added time.
     *
     * @return the added time
     */
    public long getAddedTime() {
        return addedTime;
    }

    /**
     * Sets added time.
     *
     * @param addedTime the added time
     */
    public void setAddedTime(long addedTime) {
        this.addedTime = addedTime;
    }

    /**
     * Gets started player uuid.
     *
     * @return the started player uuid
     */
    public UUID getStartedPlayerUUID() {
        return startedPlayerUUID;
    }

    /**
     * Sets started player uuid.
     *
     * @param startedPlayerUUID the started player uuid
     */
    public void setStartedPlayerUUID(UUID startedPlayerUUID) {
        this.startedPlayerUUID = startedPlayerUUID;
    }

    /**
     * Is claimed boolean.
     *
     * @return the boolean
     */
    public boolean isClaimed() {
        return claimed;
    }

    /**
     * Sets claimed.
     *
     * @param claimed the claimed
     */
    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
