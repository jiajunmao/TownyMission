package world.naturecraft.townymission.listeners;

import com.Zrips.CMI.events.CMIUserBalanceChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.Money;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.TownyUtil;

/**
 * The type Money listener.
 */
public class MoneyListener extends TownyMissionListener {

    /**
     * Instantiates a new Money listener.
     *
     * @param instance the instance
     */
    public MoneyListener(TownyMission instance) {
        super(instance);
    }

    /**
     * On money receive.
     *
     * @param event the event
     */
    @EventHandler
    public void onMoneyReceive(CMIUserBalanceChangeEvent event) {
        Player player = event.getUser().getPlayer();

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
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
        };

        r.runTaskAsynchronously(instance);
    }

    /**
     * Sanity checking the command
     * Assume that sender is player when the command is invoked
     *
     * @param player the player
     * @param event  the event
     * @return true if sanity checks pass
     */
    public boolean sanityCheck(Player player, CMIUserBalanceChangeEvent event) {

        Town town;
        if ((town = TownyUtil.residentOf(player)) != null) {
            TaskEntry entry = taskDao.getStartedMission(town);
            if (entry != null) {
                if (entry.getTaskType().equalsIgnoreCase(MissionType.MONEY.name())) {
                    if (event.getSource() == null) {
                        double diff = event.getTo() - event.getFrom();
                        return diff > 0;
                    } else {
                        System.out.println("Money comes from player");
                        return false;
                    }
                } else {
                    System.out.println("Started task type is not MONEY");
                    return false;
                }
            } else {
                System.out.println("Has no started task");
                return false;
            }
        } else {
            return false;
        }
    }

}
