package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.services.TaskBukkitService;
import world.naturecraft.townymission.bungee.services.TaskBungeeService;

public abstract class TaskService extends TownyMissionService {

    private static TaskService executor;

    public static TaskService getInstance() {
        if (executor == null) {
            if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
                executor = TaskBukkitService.getInstance();
            } else {
                executor = TaskBungeeService.getInstance();
            }
        }

        return executor;
    }

    public abstract void runTaskAsync(Runnable r);

    public abstract void runTimerTaskAsync(Runnable r, long delay, long period);
}
