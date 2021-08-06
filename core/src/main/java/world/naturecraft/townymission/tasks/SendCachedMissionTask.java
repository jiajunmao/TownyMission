package world.naturecraft.townymission.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.naturelib.components.DataHolder;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.PluginMessage;
import world.naturecraft.townymission.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.components.enums.ResendMethod;
import world.naturecraft.townymission.data.dao.MissionCacheDao;
import world.naturecraft.townymission.services.PluginMessagingService;
import world.naturecraft.townymission.utils.Util;

import java.util.Date;
import java.util.List;
import java.util.Locale;
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
                if (!previousComplete.getData()) return;

                previousComplete.setData(false);
                TownyMissionInstance.getInstance().getInstanceLogger().info("Attempting to send cached missions");
                List<MissionCacheEntry> missionCacheEntryList = MissionCacheDao.getInstance().getEntries();
                int totalSent = 0;
                int totalCache = missionCacheEntryList.size();
                for (MissionCacheEntry entry : missionCacheEntryList) {
                    TownyMissionInstance instance = TownyMissionInstance.getInstance();

                    // Check whether we should send this
                    int interval = instance.getInstanceConfig().getInt("bungeecord.resend-interval");
                    String resendMethodStr = instance.getInstanceConfig().getString("bungeecord.resend-method");
                    ResendMethod resendMethod = ResendMethod.valueOf(resendMethodStr.toUpperCase(Locale.ROOT));

                    switch (resendMethod) {
                        case INCREMENTAL:
                            long timeInterval = TimeUnit.MILLISECONDS.convert(entry.getRetryCount() * interval, TimeUnit.SECONDS);
                            long lastAttempt = entry.getLastAttempted();
                            if (lastAttempt + timeInterval > new Date().getTime()) {
                                continue;
                            }
                            break;
                        case FIXED:
                            timeInterval = TimeUnit.MILLISECONDS.convert(interval, TimeUnit.SECONDS);
                            lastAttempt = entry.getLastAttempted();
                            if (lastAttempt + timeInterval > new Date().getTime()) {
                                continue;
                            }
                            break;
                    }


                    PluginMessage mainSrvRequest = new PluginMessage()
                            .origin(instance.getInstanceConfig().getString("bungeecord.server-name"))
                            .destination(instance.getInstanceConfig().getString("bungeecord.server-name"))
                            .channel("config:request")
                            .messageUUID(UUID.randomUUID())
                            .dataSize(1)
                            .data(new String[]{"main-server"});

                    String mainServer = null;

                    try {
                        PluginMessage mainSrvResponse = PluginMessagingService.getInstance().sendAndWait(mainSrvRequest, 5, TimeUnit.SECONDS);
                        mainServer = mainSrvResponse.getData()[0];

                        PluginMessage request = new PluginMessage()
                                .origin(instance.getInstanceConfig().getString("bungeecord.server-name"))
                                .destination(mainServer)
                                .channel("mission:request")
                                .messageUUID(UUID.randomUUID())
                                .dataSize(4)
                                .data(new String[]{"doMission", entry.getPlayerUUID().toString(), entry.getMissionType().name(), String.valueOf(entry.getAmount())});

                        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request, 5, TimeUnit.SECONDS);
                        MissionCacheDao.getInstance().remove(entry);
                        totalSent++;
                    } catch (TimeoutException | ExecutionException | InterruptedException e) {
                        // This means that the sending keeps failing for some reason
                        //    Increment failed count (retryCount and lastAttempted)
                        entry.setLastAttempted(new Date().getTime());
                        entry.setRetryCount(entry.getRetryCount() + 1);
                        MissionCacheDao.getInstance().update(entry);
                        //    Check whether there is hard limit on fail count
                        String hardDiscardStr = instance.getInstanceConfig().getString("bungeecord.hard-discard");
                        if (Util.isInt(hardDiscardStr) && Integer.parseInt(hardDiscardStr) > 0) {
                            // This means that a hard discard limit is set
                            if (entry.getRetryCount() > Integer.parseInt(hardDiscardStr)) {
                                MissionCacheDao.getInstance().remove(entry);
                            }
                        }
                    }
                }

                TownyMissionInstance.getInstance().getInstanceLogger().warning("Cache send attempted, successfully sent " + totalSent + ", total cached " + totalCache);
                previousComplete.setData(true);
            }
        };

        // Looping every minute
        int intervalS = TownyMissionInstance.getInstance().getInstanceConfig().getInt("bungeecord.resend-interval");
        r.runTaskTimerAsynchronously(TownyMissionInstance.getInstance(), 5 * 20, intervalS * 20L);
    }
}
