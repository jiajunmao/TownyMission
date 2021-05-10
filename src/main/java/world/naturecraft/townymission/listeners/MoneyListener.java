package world.naturecraft.townymission.listeners;

import com.Zrips.CMI.events.CMIUserBalanceChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.Money;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

/**
 * The type Money listener.
 */
public class MoneyListener extends TownyMissionListener {

    public MoneyListener(TownyMission instance) {
        super(instance);
    }

    @EventHandler
    public void onMoneyReceive(CMIUserBalanceChangeEvent event) {
        Player player = event.getUser().getPlayer();

        if (sanityCheck(player, event)) {
            Town town = TownyUtil.residentOf(player);
            TaskEntry moneyTask = taskDao.getTownTasks(town, MissionType.MONEY).get(0);

            Money money;
            try {
                money = Money.parse(moneyTask.getTaskJson());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            }

            int diff = (int) (event.getTo() - event.getFrom());
            money.setCompleted(money.getCompleted() + diff);
            try {
                moneyTask.setTaskJson(money.toJson());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            }

            taskDao.update(moneyTask);
        } else {
            instance.getLogger().severe("Money event did not pass sanity check");
        }
    }

    /**
     * Sanity checking the command
     * Assume that sender is player when the command is invoked
     *
     * @param player the player
     * @return true if sanity checks pass
     */
    public boolean sanityCheck(Player player, CMIUserBalanceChangeEvent event) {

        Town town;
        if ((town = TownyUtil.residentOf(player)) != null) {
            if (taskDao.hasStartedMission(town)) {
                System.out.println("Size of entry list: " + taskDao.getTownTasks(town, MissionType.MONEY).size());
                if (taskDao.getTownTasks(town, MissionType.MONEY).size() >= 1) {
                    double diff = event.getTo() - event.getFrom();
                    System.out.println("diff: " + diff);
                    if (diff > 0) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
