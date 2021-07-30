package world.naturecraft.townymission.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.utils.BukkitUtil;
import world.naturecraft.townymission.utils.UpdateChecker;

public class UpdateRemindListener extends TownyMissionListener {
    /**
     * Instantiates a new Towny mission listener.
     *
     * @param instance the instance
     */
    public UpdateRemindListener(TownyMissionBukkit instance) {
        super(instance);
    }

    @EventHandler
    public void onAdminJoin(PlayerJoinEvent e) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                Player player = e.getPlayer();
                if (player.isOp()) {
                    new UpdateChecker(instance, 94472).getVersion(version -> {
                        version = version.substring(1);
                        if (!UpdateChecker.isGreater(instance.getDescription().getVersion(), version)) {
                            String str = "[TownyMission] &bThere is a an update available! Please visit Spigot resource page to download! Current version: " + "&f" + instance.getDescription().getVersion() + ", &bLatest version: " + "&f" + version;
                            instance.getServer().getConsoleSender().sendMessage(BukkitUtil.translateColor(str));
                        }
                    });
                }
            }
        };

        r.runTaskAsynchronously(instance);
    }
}
