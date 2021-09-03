package world.naturecraft.townymission.listeners.mission.vote;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import teozfrank.ultimatevotes.events.VoteRewardEvent;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.listeners.mission.MissionListener;
import world.naturecraft.townymission.utils.BukkitChecker;

public class UltimateVoteListener extends MissionListener {


    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public UltimateVoteListener(TownyMissionBukkit instance) {
        super(instance);
    }

    /**
     * On vote received.
     *
     * @param e the e
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVoteReceived(VoteRewardEvent e) {
        Player player = e.getPlayer();

        BukkitChecker bukkitChecker = null;
        if (instance.isMainServer()) {
            bukkitChecker = new BukkitChecker(instance).target(player).silent(true)
                    .hasTown()
                    .hasStarted()
                    .isMissionType(MissionType.VOTE);
        }

        doLogic(bukkitChecker, MissionType.VOTE, player, e.getUnclaimedCount());
    }
}
