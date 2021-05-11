package world.naturecraft.townymission.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import teozfrank.ultimatevotes.events.VoteRewardEvent;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.Vote;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

/**
 * The type Vote listener.
 */
public class VoteListener extends TownyMissionListener {
    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public VoteListener(TownyMission instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVoteReceived(VoteRewardEvent e) {
        if (sanityCheck(e.getPlayer())) {
            TaskEntry entry = taskDao.getStartedMission(TownyUtil.residentOf(e.getPlayer()));

            Vote voteJson;
            try {
                voteJson = Vote.parse(entry.getTaskJson());
                voteJson.setCompleted(voteJson.getCompleted() + e.getVoteCount());
                entry.setTaskJson(voteJson);
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
                return;
            }

            taskDao.update(entry);
        } else {
            System.out.println("Vote sanity check failed");
        }
    }

    public boolean sanityCheck(Player player) {
        SanityChecker sanityChecker = new SanityChecker(instance);
        return sanityChecker.target(player)
                .hasTown()
                .hasStarted()
                .isMissionType(MissionType.VOTE)
                .check();
    }
}
