package world.naturecraft.townymission.listeners.mission.vote;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.listeners.mission.MissionListener;
import world.naturecraft.townymission.utils.BukkitChecker;

public class NuVotifierVoteListener extends MissionListener {

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public NuVotifierVoteListener(TownyMissionBukkit instance) {
        super(TownyMissionInstance.getInstance());
    }

    @EventHandler(priority= EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {
        Vote vote = event.getVote();

        /*
         * Process Vote record as you see fit
         */
        OfflinePlayer player = Bukkit.getOfflinePlayer(vote.getUsername());
        BukkitChecker checker = null;
        if (instance.isMainServer()) {
            checker = new BukkitChecker(instance).target(player).silent(true)
                    .hasTown()
                    .hasStarted()
                    .isMissionType(MissionType.VOTE);
        }

        doLogic(checker, MissionType.VOTE, player, 1);
    }

}
