package world.naturecraft.townymission.listeners.mission.mob;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.json.mission.MobMissionJson;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.listeners.mission.MissionListener;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

public class MythicMobListener extends MissionListener {
    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public MythicMobListener(TownyMissionBukkit instance) {
        super(instance);
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        LivingEntity killer = e.getKiller();

        if (killer instanceof Player) {
            Player playerKiller = (Player) killer;
            BukkitChecker checker = null;
            if (instance.isMainServer()) {
                checker = new BukkitChecker(instance).target(playerKiller).silent(true)
                        .hasTown()
                        .hasStarted()
                        .isMissionType(MissionType.MOB)
                        .customCheck(() -> {
                            MissionEntry taskEntry = MissionDao.getInstance().getEntries(missionEntry -> missionEntry.getTownUUID().equals(TownyUtil.residentOf(playerKiller).getUUID()) &&
                                    missionEntry.getMissionType().equals(MissionType.MOB)).get(0);
                            MobMissionJson mobMissionJson = (MobMissionJson) taskEntry.getMissionJson();
                            if (!mobMissionJson.isMm()) return false;

                            return mobMissionJson.getEntityType().equalsIgnoreCase(e.getMobType().getInternalName());
                        });
            }

            doLogic(checker, MissionType.MOB, playerKiller, 1);
        }
    }
}
