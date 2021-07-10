package world.naturecraft.townymission.listeners.mission;

import com.palmergames.bukkit.towny.event.TownClaimEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.event.EventHandler;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.dao.MissionDao;

public class ExpansionListener extends MissionListener {

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public ExpansionListener(TownyMissionBukkit instance) {
        super(instance);
    }

    /**
     * On town expansion.
     *
     * @param e the e
     */
    @EventHandler
    public void onTownExpansion(TownClaimEvent e) {
        BukkitChecker checker = null;

        if (instance.isMainserver()) {
            checker = new BukkitChecker(instance).target(e.getResident().getPlayer()).silent(true)
                    .hasTown()
                    .hasStarted()
                    .isMissionType(MissionType.EXPANSION)
                    .customCheck(() -> {
                        try {
                            MissionEntry entry = MissionDao.getInstance().getTownStartedMission(e.getTownBlock().getTown().getUUID(), MissionType.EXPANSION);
                            return entry.getTownUUID().equals(e.getTownBlock().getTown().getUUID());
                        } catch (NotRegisteredException notRegisteredException) {
                            return false;
                        }

                    });
        }

        doLogic(checker, MissionType.EXPANSION, e.getResident().getPlayer(), 1);
    }
}