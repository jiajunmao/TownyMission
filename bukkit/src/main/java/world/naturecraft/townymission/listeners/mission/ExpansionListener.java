package world.naturecraft.townymission.listeners.mission;

import com.palmergames.bukkit.towny.event.TownClaimEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.EventHandler;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

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

        if (instance.isMainServer()) {
            checker = new BukkitChecker(instance).target(e.getResident().getPlayer()).silent(true)
                    .hasTown()
                    .hasStarted()
                    .isMissionType(MissionType.EXPANSION)
                    .customCheck(() -> {
                        try {
                            Town currTown = e.getTownBlock().getTown();
                            return MissionDao.getInstance().getTownMissions(TownyUtil.residentOf(e.getResident().getPlayer()).getUUID(), missionEntry -> (missionEntry.getTownUUID().equals(currTown.getUUID()))).size() != 0;
                        } catch (NotRegisteredException notRegisteredException) {
                            return false;
                        }

                    });
        }

        doLogic(checker, MissionType.EXPANSION, e.getResident().getPlayer(), 1);
    }
}
