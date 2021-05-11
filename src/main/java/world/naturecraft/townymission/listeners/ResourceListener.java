package world.naturecraft.townymission.listeners;

import com.Zrips.CMI.events.CMIUserBalanceChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.Money;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.TownyUtil;

/**
 * The type Resource listener.
 */
public class ResourceListener extends TownyMissionListener {

    /**
     * Instantiates a new resource listener.
     *
     * @param instance the instance
     */
    public ResourceListener(TownyMission instance) {
        super(instance);
    }

    /**
     * On resource collected.
     *
     * @param event the event
     */
    @EventHandler
    public void onResourceCollected() {
        Player player;
        Town town = TownyUtil.residentOf(player);
        if
    }

}
