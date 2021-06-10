package world.naturecraft.townymission.components.containers.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.components.containers.json.MissionJson;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.MissionJsonFactory;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.UUID;

/**
 * The type Task history entry.
 */
public class MissionHistoryEntry extends SqlEntry {

    private MissionType missionType;
    private long addedTime;
    private long startedTime;
    private long allowedTime;
    private MissionJson missionJson;
    private Town town;
    private Player startedPlayer;
    private long completedTime;
    private boolean claimed;
    private int sprint;
    private int season;

    /**
     * Instantiates a new Task history entry.
     *
     * @param id            the id
     * @param missionType   the task type
     * @param addedTime     the added time
     * @param startedTime   the started time
     * @param allowedTime   the allowed time
     * @param missionJson   the task json
     * @param town          the town
     * @param startedPlayer the started player
     * @param completedTime the completed time
     * @param sprint        the sprint
     * @param season        the season
     */
    public MissionHistoryEntry(int id, MissionType missionType, long addedTime, long startedTime, long allowedTime,
                               MissionJson missionJson, Town town, Player startedPlayer, long completedTime, boolean claimed,
                               int sprint, int season) {
        super(id, DbType.TASK_HISTORY);
        this.missionType = missionType;
        this.addedTime = addedTime;
        this.startedTime = startedTime;
        this.allowedTime = allowedTime;
        this.missionJson = missionJson;
        this.town = town;
        this.startedPlayer = startedPlayer;
        this.completedTime = completedTime;
        this.claimed = claimed;
        this.sprint = sprint;
        this.season = season;
    }

    public MissionHistoryEntry(int id, String missionType, long addedTime, long startedTime, long allowedTime,
                               String missionJson, String townName, String startedPlayer, long completedTime,
                               boolean claimed, int sprint, int season) throws JsonProcessingException {
        this(id, MissionType.valueOf(missionType), addedTime, startedTime, allowedTime, null, TownyUtil.getTownByName(townName), Bukkit.getPlayer(UUID.fromString(startedPlayer)), completedTime, claimed, sprint, season);
        this.missionJson = MissionJsonFactory.getJson(missionJson, MissionType.valueOf(missionType));
    }

    public MissionHistoryEntry(MissionEntry entry, long completedTime) {
        this(0, entry.getMissionType(), entry.getAddedTime(), entry.getStartedTime(), entry.getAllowedTime(),
                entry.getMissionJson(), entry.getTown(), entry.getStartedPlayer(), completedTime, false, 0, 0);
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
     * Sets task json.
     *
     * @param missionJson the task json
     */
    public void setTaskJson(MissionJson missionJson) {
        this.missionJson = missionJson;
    }

    /**
     * Gets town.
     *
     * @return the town
     */
    public Town getTown() {
        return town;
    }

    /**
     * Sets town.
     *
     * @param town the town
     */
    public void setTown(Town town) {
        this.town = town;
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
     * Gets started player.
     *
     * @return the started player
     */
    public Player getStartedPlayer() {
        return startedPlayer;
    }

    /**
     * Sets started player.
     *
     * @param startedPlayer the started player
     */
    public void setStartedPlayer(Player startedPlayer) {
        this.startedPlayer = startedPlayer;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
