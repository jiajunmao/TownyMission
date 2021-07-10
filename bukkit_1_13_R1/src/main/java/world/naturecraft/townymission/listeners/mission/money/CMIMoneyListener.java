package world.naturecraft.townymission.listeners.mission.money;

import com.Zrips.CMI.events.CMIUserBalanceChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.listeners.mission.MissionListener;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.services.EconomyService;

public class CMIMoneyListener extends MissionListener {
    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public CMIMoneyListener(TownyMissionBukkit instance) {
        super(instance);
    }

    /**
     * On money receive.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMoneyReceive(CMIUserBalanceChangeEvent event) {
        Player player = event.getUser().getPlayer();
        BukkitChecker checker = null;
        if (instance.isMainserver()) {
            checker = new BukkitChecker(instance).target(player).silent(true)
                    .hasTown()
                    .hasStarted()
                    .isMissionType(MissionType.MONEY)
                    .customCheck(() -> event.getSource() == null)
                    .customCheck(() -> ((event.getTo() - event.getFrom()) > 0))
                    .customCheck(() -> event.getFrom() != 0)
                    .customCheck(() -> {
                        // CHANGED LOGIC: Money will be held away from player until the mission ends
                        //   Then given back depending on the returnable setting
                        EconomyService.getInstance().withdrawBalance(player.getUniqueId(), event.getTo() - event.getFrom());

                        // Because this is giving a returnable check in doLogic, after the sanity check has passed.
                        //  This should always return true
                        return true;
                    });
        } else {
            // This is to make sure that MysqlPlayerBridge events do not cause trouble
            BukkitChecker localCheck = new BukkitChecker(instance).target(player).silent(true)
                    .customCheck(() -> (event.getTo() - event.getFrom() > 0))
                    .customCheck(() -> event.getFrom() != 0);
            if (!localCheck.check()) return;
        }

        doLogic(checker, MissionType.MONEY, event.getUser().getPlayer(), (int) (event.getTo() - event.getFrom()));
    }
}
