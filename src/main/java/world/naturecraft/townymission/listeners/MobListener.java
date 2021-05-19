package world.naturecraft.townymission.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.MobJson;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.SanityChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.Locale;

public class MobListener extends TownyMissionListener {

    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public MobListener(TownyMission instance) {
        super(instance);
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        LivingEntity dead = e.getEntity();
        Player killer = e.getEntity().getKiller();

        if (killer != null) {
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    boolean sane = new SanityChecker(instance).target(killer)
                            .hasTown()
                            .hasStarted()
                            .isMissionType(MissionType.MOB)
                            .customCheck(() -> {
                                return taskDao.getStartedMission(TownyUtil.residentOf(killer)).getTaskType().equalsIgnoreCase(MissionType.MOB.name());
                            })
                            .check();

                    if (sane) {
                        TaskEntry taskEntry = taskDao.getStartedMission(TownyUtil.residentOf(killer));
                        try {
                            MobJson mobJson = MobJson.parse(taskEntry.getTaskJson());
                            mobJson.setCompleted(mobJson.getCompleted() + 1);
                            taskEntry.setTaskJson(mobJson);
                        } catch (JsonProcessingException exp) {
                            instance.getLogger().severe("Error parsing json: " + taskEntry.getTaskJson());
                            exp.printStackTrace();
                            return;
                        }

                        taskDao.update(taskEntry);
                    }
                }
            };

            r.runTaskAsynchronously(instance);
        }
    }
}