package world.naturecraft.townymission.listeners.mission;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import world.naturecraft.naturelib.utils.EntryFilter;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.json.mission.MobMissionJson;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

public class MobListener extends MissionListener {
    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public MobListener(TownyMissionBukkit instance) {
        super(instance);
    }

    /**
     * On mob kill.
     *
     * @param e the e
     */
    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        LivingEntity dead = e.getEntity();
        Player killer = e.getEntity().getKiller();

        if (killer != null) {
            BukkitChecker checker = null;
            if (instance.isMainServer()) {
                checker = new BukkitChecker(instance).target(killer).silent(true)
                        .hasTown()
                        .hasStarted()
                        .isMissionType(MissionType.MOB)
                        .customCheck(() -> {
                            MissionEntry taskEntry = MissionDao.getInstance().getEntries(missionEntry -> missionEntry.getTownUUID().equals(TownyUtil.residentOf(killer).getUUID()) &&
                                    missionEntry.getMissionType().equals(MissionType.MOB)).get(0);
                            MobMissionJson mobMissionJson = (MobMissionJson) taskEntry.getMissionJson();
                            return EntityType.valueOf(mobMissionJson.getEntityType()).equals(dead.getType());
                        });
            }

            doLogic(checker, MissionType.MOB, killer, 1);
        }
    }
}
