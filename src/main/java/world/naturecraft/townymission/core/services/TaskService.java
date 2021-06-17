package world.naturecraft.townymission.core.services;

import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bungee.TownyMissionBungee;
import world.naturecraft.townymission.core.components.enums.ServerType;

/**
 * The type Task service.
 */
public class TaskService {

    /**
     * Run task async.
     *
     * @param r the r
     */
    public static void runTaskAsync(BukkitRunnable r) {
        TownyMissionBukkit townyMissionBukkit = TownyMissionInstance.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(townyMissionBukkit, r);
    }

    /**
     * Run task async.
     *
     * @param r the r
     */
    public static void runTaskAsync(Runnable r) {
        TownyMissionBungee townyMissionBungee = TownyMissionInstance.getInstance();
        ProxyServer.getInstance().getScheduler().runAsync(townyMissionBungee, r);
    }

    /**
     * Run task.
     *
     * @param r the r
     */
    public static void runTask(BukkitRunnable r) {
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
    public static void runTaskTimerAsync(BukkitRunnable r, long delay, long period) {
        TownyMissionBukkit townyMissionBukkit = TownyMissionInstance.getInstance();
        Bukkit.getScheduler().runTaskTimerAsynchronously(townyMissionBukkit, r, delay, period);
    }
}
