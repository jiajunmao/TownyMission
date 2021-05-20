package world.naturecraft.townymission.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.event.TownClaimEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.EventHandler;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.ExpansionJson;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;

/**
 * The type Expansion listener.
 */
public class ExpansionListener extends TownyMissionListener {

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public ExpansionListener(TownyMission instance) {
        super(instance);
    }

    @EventHandler
    public void onTownExpansion(TownClaimEvent e) {
        Town town;
        try {
            town = e.getTownBlock().getTown();
        } catch (NotRegisteredException exception) {
            instance.getLogger().severe(exception.getLocalizedMessage());
            exception.printStackTrace();
            return;
        }


        if (taskDao.getStartedMission(town) != null && taskDao.getStartedMission(town).getTaskType().equalsIgnoreCase(MissionType.EXPANSION.name())) {
            TaskEntry taskEntry = taskDao.getStartedMission(town);
            try {
                ExpansionJson expansionJson = ExpansionJson.parse(taskEntry.getTaskJson());
                expansionJson.setCompleted(expansionJson.getCompleted() + 1);
                taskEntry.setTaskJson(expansionJson);
            } catch (JsonProcessingException exception) {
                instance.getLogger().severe("Error parsing Json: " + taskEntry.getTaskJson());
                exception.printStackTrace();
                return;
            }

            taskDao.update(taskEntry);
        }
    }
}
