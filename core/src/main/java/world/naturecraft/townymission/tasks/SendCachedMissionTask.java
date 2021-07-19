package world.naturecraft.townymission.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.exceptions.PMCReceiveException;
import world.naturecraft.townymission.components.DataHolder;
import world.naturecraft.townymission.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.components.PluginMessage;
import world.naturecraft.townymission.data.dao.MissionCacheDao;
import world.naturecraft.townymission.services.PluginMessagingService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SendCachedMissionTask {

    public static void registerTask() {
        final DataHolder<Boolean> previousComplete = new DataHolder<>();
        previousComplete.setData(true);

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (previousComplete.getData() == false) return;

                previousComplete.setData(false);
                TownyMissionInstance.getInstance().getInstanceLogger().info("Attempting to send cached missions");
                List<MissionCacheEntry> missionCacheEntryList = MissionCacheDao.getInstance().getEntries();
                int totalSent = 0;
                int totalCache = missionCacheEntryList.size();
                for (MissionCacheEntry entry : missionCacheEntryList) {
                    PluginMessage request = new PluginMessage()
                            .channel("mission:request")
                            .messageUUID(UUID.randomUUID())
                            .dataSize(4)
                            .data(new String[]{"doMission", entry.getPlayerUUID().toString(), entry.getMissionType().name(), String.valueOf(entry.getAmount())});

                    try {
                        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request, 5, TimeUnit.SECONDS);
                        MissionCacheDao.getInstance().remove(entry);
                        totalSent++;
                    } catch (TimeoutException | ExecutionException | InterruptedException e) {
                        // This means that the sending keeps failing for some reason
                    }
                }

                TownyMissionInstance.getInstance().getInstanceLogger().warning("Cache send attempted, successfully sent " + totalSent + ", total cached " + totalCache);
                previousComplete.setData(true);
            }
        };

        // Looping every minute
        r.runTaskTimerAsynchronously(TownyMissionInstance.getInstance(), 5 * 20, 10 * 20);
    }
}
