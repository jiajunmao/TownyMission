package world.naturecraft.townymission.bungee.services;

import net.md_5.bungee.api.ProxyServer;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bungee.TownyMissionBungee;
import world.naturecraft.townymission.core.services.TaskService;

import java.util.concurrent.TimeUnit;

public class TaskBungeeService extends TaskService {

    private static TaskBungeeService singleton;

    public static TaskBungeeService getInstance() {
        if (singleton == null) {
            singleton = new TaskBungeeService();
        }

        return singleton;
    }
    /**
     * Run task async.
     *
     * @param r the r
     */
    public void runTaskAsync(Runnable r) {
        TownyMissionBungee townyMissionBungee = TownyMissionInstance.getInstance();
        ProxyServer.getInstance().getScheduler().runAsync(townyMissionBungee, r);
    }

    public void runTimerTaskAsync(Runnable r, long delay, long period) {
        TownyMissionBungee townyMissionBungee = TownyMissionInstance.getInstance();
        ProxyServer.getInstance().getScheduler().schedule(townyMissionBungee, r, delay, period, TimeUnit.MILLISECONDS);
    }
}
