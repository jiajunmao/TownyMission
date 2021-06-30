package world.naturecraft.townymission.bukkit.services;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.core.services.TaskService;

/**
 * The type Task service.
 */
public class TaskBukkitService extends TaskService {

    private static TaskBukkitService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TaskBukkitService getInstance() {
        if (singleton == null) {
            singleton = new TaskBukkitService();
        }

        return singleton;
    }

    /**
     * Run task async.
     *
     * @param r the r
     */
    public void runTaskAsync(Runnable r) {
        TownyMissionBukkit townyMissionBukkit = TownyMissionInstance.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(townyMissionBukkit, r);
    }

    /**
     * Run task.
     *
     * @param r the r
     */
    @Override
    public void runTask(Runnable r) {
        TownyMissionBukkit townyMissionBukkit = TownyMissionInstance.getInstance();
        Bukkit.getScheduler().runTask(townyMissionBukkit, r);
    }

    /**
     * Run task timer async.
     *
     * @param r      the r
     * @param delay  the delay
     * @param period the period
     */
    public void runTimerTaskAsync(Runnable r, long delay, long period) {
        TownyMissionBukkit townyMissionBukkit = TownyMissionInstance.getInstance();
        Bukkit.getScheduler().runTaskTimerAsynchronously(townyMissionBukkit, r, delay, period);
    }
}
