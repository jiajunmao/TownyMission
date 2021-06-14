package world.naturecraft.townymission.components.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.reward.*;

import java.util.UUID;

/**
 * The type Claim entry.
 */
public class ClaimEntry extends DataEntity {

    private UUID playerUUID;
    private RewardJson rewardJson;
    private int sprint;
    private int season;

    /**
     * Instantiates a new Sql entry.
     *
     * @param id         the id
     * @param playerUUID the player uuid
     * @param rewardJson the reward json
     * @param season     the season
     * @param sprint     the sprint
     */
    public ClaimEntry(UUID id, UUID playerUUID, RewardJson rewardJson, int season, int sprint) {
        super(id, DbType.CLAIM);
        this.playerUUID = playerUUID;
        this.rewardJson = rewardJson;
        this.season = season;
        this.sprint = sprint;
    }

    /**
     * Instantiates a new Claim entry.
     *
     * @param id         the id
     * @param playerUUID the player uuid
     * @param rewardJson the reward json
     * @param season     the season
     * @param sprint     the sprint
     */
    public ClaimEntry(String id, String playerUUID, String rewardJson, int season, int sprint) {
        this(UUID.fromString(id), UUID.fromString(playerUUID), null, season, sprint);
        try {
            RewardJson temp = RewardJson.parse(rewardJson);
            RewardType rewardType = temp.getRewardType();
            switch (rewardType) {
                case RESOURCE:
                    setRewardJson(ResourceRewardJson.parse(rewardJson));
                    break;
                case MONEY:
                    setRewardJson(MoneyRewardJson.parse(rewardJson));
                    break;
                case POINTS:
                    setRewardJson(PointRewardJson.parse(rewardJson));
                    break;
                case COMMAND:
                    setRewardJson(CommandRewardJson.parse(rewardJson));
                    break;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets player uuid.
     *
     * @return the player uuid
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Sets player uuid.
     *
     * @param player the player
     */
    public void setPlayerUUID(Player player) {
        this.playerUUID = playerUUID;
    }

    /**
     * Gets reward json.
     *
     * @return the reward json
     */
    public RewardJson getRewardJson() {
        return rewardJson;
    }

    /**
     * Sets reward json.
     *
     * @param rewardJson the reward json
     */
    public void setRewardJson(RewardJson rewardJson) {
        this.rewardJson = rewardJson;
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
}
