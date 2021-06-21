package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstanceType;
import world.naturecraft.townymission.bukkit.services.TaskBukkitService;
import world.naturecraft.townymission.bungee.services.TaskBungeeService;

/**
 * The type Task service.
 */
public abstract class TaskService extends TownyMissionService {

    private static TaskService executor;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TaskService getInstance() {
        if (executor == null) {
            if (TownyMissionInstanceType.isBukkit()) {
                executor = TaskBukkitService.getInstance();
            } else {
                executor = TaskBungeeService.getInstance();
            }
        }

        return executor;
    }

    /**
     * Run task async.
     *
     * @param r the r
     */
    public abstract void runTaskAsync(Runnable r);

    /**
     * Run timer task async.
     *
     * @param r      the r
     * @param delay  the delay
     * @param period the period
     */
    public abstract void runTimerTaskAsync(Runnable r, long delay, long period);
}
