package world.naturecraft.townymission.listeners.mission.money;

import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.listeners.mission.MissionListener;
import world.naturecraft.townymission.services.EconomyService;
import world.naturecraft.townymission.utils.BukkitChecker;

import java.math.BigDecimal;

public class EssentialMoneyListener extends MissionListener {
    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public EssentialMoneyListener(TownyMissionBukkit instance) {
        super(instance);
    }

    @EventHandler
    public void onMoneyReceived(UserBalanceUpdateEvent event) {
        Player player = event.getPlayer();
        BukkitChecker checker = null;

        if (instance.isMainServer()) {
            checker = new BukkitChecker(instance).target(player).silent(true)
                    .hasTown()
                    .hasStarted()
                    .isMissionType(MissionType.MONEY)
                    .customCheck(() -> {
                        return !event.getCause().equals(UserBalanceUpdateEvent.Cause.COMMAND_ECO)
                                && !event.getCause().equals(UserBalanceUpdateEvent.Cause.COMMAND_PAY)
                                && !event.getCause().equals(UserBalanceUpdateEvent.Cause.COMMAND_SELL);
                    })
                    .customCheck(() -> {
                        return (event.getNewBalance().subtract(event.getOldBalance()).compareTo(BigDecimal.ZERO) > 0);
                    })
                    .customCheck(() -> {
                        return event.getOldBalance().compareTo(BigDecimal.ZERO) != 0;
                    })
                    .customCheck(() -> {
                        // CHANGED LOGIC: Money will be held away from player until the mission ends
                        //   Then given back depending on the returnable setting
                        EconomyService.getInstance().withdrawBalance(player.getUniqueId(), event.getOldBalance().subtract(event.getNewBalance()).intValue());

                        // Because this is giving a returnable check in doLogic, after the sanity check has passed.
                        //  This should always return true
                        return true;
                    });
        } else {
            BukkitChecker localCheck = new BukkitChecker(instance).target(player).silent(true)
                    .customCheck(() -> {
                        return !event.getCause().equals(UserBalanceUpdateEvent.Cause.COMMAND_ECO)
                                && !event.getCause().equals(UserBalanceUpdateEvent.Cause.COMMAND_PAY)
                                && !event.getCause().equals(UserBalanceUpdateEvent.Cause.COMMAND_SELL);
                    })
                    .customCheck(() -> {
                        return (event.getNewBalance().subtract(event.getOldBalance()).compareTo(BigDecimal.ZERO) > 0);
                    });

            if (!localCheck.check()) return;
        }

        doLogic(checker, MissionType.MONEY, player, (event.getNewBalance().subtract(event.getOldBalance())).intValue());
    }
}
