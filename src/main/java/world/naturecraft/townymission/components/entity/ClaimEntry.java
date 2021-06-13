package world.naturecraft.townymission.components.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.reward.*;

import java.util.UUID;

public class ClaimEntry extends DataEntity {

    private Player player;
    private RewardJson rewardJson;
    private int sprint;
    private int season;
    /**
     * Instantiates a new Sql entry.
     *
     * @param id   the id
     */
    public ClaimEntry(UUID id, Player player, RewardJson rewardJson, int season, int sprint) {
        super(id, DbType.CLAIM);
        this.player = player;
        this.rewardJson = rewardJson;
        this.season = season;
        this.sprint = sprint;
    }

    public ClaimEntry(String id, String playerUUID, String rewardJson, int season, int sprint) {
        this(UUID.fromString(id), Bukkit.getPlayer(UUID.fromString(playerUUID)), null, season, sprint);
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public RewardJson getRewardJson() {
        return rewardJson;
    }

    public void setRewardJson(RewardJson rewardJson) {
        this.rewardJson = rewardJson;
    }

    public int getSprint() {
        return sprint;
    }

    public void setSprint(int sprint) {
        this.sprint = sprint;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }
}
