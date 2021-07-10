package world.naturecraft.townymission.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.exceptions.PMCReceiveException;
import world.naturecraft.townymission.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.components.entity.PluginMessage;
import world.naturecraft.townymission.data.dao.MissionCacheDao;
import world.naturecraft.townymission.services.PluginMessagingService;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SendCachedMissionTask {

    public static void registerTask() {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {

                for (MissionCacheEntry entry : MissionCacheDao.getInstance().getEntries()) {
                    PluginMessage request = new PluginMessage()
                            .channel("mission:request")
                            .messageUUID(UUID.randomUUID())
                            .dataSize(4)
                            .data(new String[]{"doMission", entry.getPlayerUUID().toString(), entry.getMissionType().name(), String.valueOf(entry.getAmount())});

                    try {
                        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request, 5, TimeUnit.SECONDS);
                        if (response.getData()[0].equalsIgnoreCase("false")) {
                            throw new PMCReceiveException("Something went wrong on the PMC receiver");
                        } else {
                            // This means this cache got sent
                            MissionCacheDao.getInstance().remove(entry);
                        }
                    } catch (TimeoutException | ExecutionException | InterruptedException | PMCReceiveException e) {
                        // This means that the sending keeps failing for some reason lol
                        //   Do nothing
                    }
                }
            }
        };

        // Looping every minute
        r.runTaskTimerAsynchronously(TownyMissionInstance.getInstance(), 60*20, 1*60*20);
    }
}
