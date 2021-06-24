package world.naturecraft.townymission.bukkit.listeners.external.mission;

import com.Zrips.CMI.events.CMIUserBalanceChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.core.components.enums.MissionType;

public class MoneyListener extends MissionListener {
    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public MoneyListener(TownyMissionBukkit instance) {
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
                    .customCheck(() -> (event.getTo() - event.getFrom() > 0));
        }

        doLogic(checker, MissionType.MONEY, event.getUser().getPlayer(), (int) (event.getTo() - event.getFrom()));
    }
}
