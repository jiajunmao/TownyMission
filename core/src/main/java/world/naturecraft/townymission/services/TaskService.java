package world.naturecraft.townymission.services;

import world.naturecraft.naturelib.InstanceType;

/**
 * The type Task service.
 */
public abstract class TaskService extends TownyMissionService {

    private static TaskService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TaskService getInstance() {
        if (singleton == null) {
            if (InstanceType.isBukkit()) {
                String packageName = TaskService.class.getPackage().getName();
                try {
                    singleton = (TaskService) Class.forName(packageName + "." + "TaskBukkitService").newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return singleton;
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

    public abstract void runTask(Runnable r);
}
