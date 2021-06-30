package world.naturecraft.townymission.bukkit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.events.DoMissionEvent;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.core.components.entity.MissionEntry;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.MissionJson;
import world.naturecraft.townymission.core.data.dao.MissionDao;
import world.naturecraft.townymission.core.services.ChatService;
import world.naturecraft.townymission.core.services.MissionService;
import world.naturecraft.townymission.core.services.TaskService;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * The type Mission bukkit service.
 */
public class MissionBukkitService extends MissionService {

    private static MissionBukkitService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionBukkitService getInstance() {
        if (singleton == null) {
            singleton = new MissionBukkitService();
        }

        return singleton;
    }

    public boolean canStartMission(UUID playerUUID) {
        TownyMissionBukkit townyMissionBukkit = TownyMissionInstance.getInstance();
        Player player = Bukkit.getPlayer(playerUUID);
        return new BukkitChecker(townyMissionBukkit).target(player)
                .hasTown()
                .hasPermission("townymission.player")
                .customCheck(() -> {
                    Town town = TownyUtil.residentOf(player);

                    if (MissionDao.getInstance().getStartedMission(town.getUUID()) == null) {
                        return true;
                    } else {
                        ChatService.getInstance().sendMsg(playerUUID, townyMissionBukkit.getLangEntry("commands.start.onAlreadyStarted"));
                        return false;
                    }
                }).check();
    }

    public boolean canAbortMission(UUID playerUUID, MissionEntry entry) {
        TownyMissionBukkit townyMissionBukkit = TownyMissionInstance.getInstance();
        Player player = Bukkit.getPlayer(playerUUID);
        BukkitChecker checker = new BukkitChecker(townyMissionBukkit).target(player)
                .hasTown()
                .hasStarted()
                .hasPermission("townymission.player")
                .customCheck(() -> {
                    if (TownyUtil.mayorOf(player) != null)
                        return true;

                    return entry.getStartedPlayerUUID().equals(playerUUID);
                });

        return checker.check();
    }

    public void doMission(UUID townUUID, UUID playerUUID, MissionType missionType, int amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        MissionEntry taskEntry = MissionDao.getInstance().getTownStartedMission(townUUID, missionType);

        if (taskEntry.isCompleted() || taskEntry.isTimedout()) return;

        MissionJson json = taskEntry.getMissionJson();
        json.setCompleted(json.getCompleted() + amount);
        json.addContribution(player.getUniqueId().toString(), amount);
        try {
            taskEntry.setMissionJson(json);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            return;
        }

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                DoMissionEvent missionEvent = new DoMissionEvent(player, taskEntry, false);
                Bukkit.getPluginManager().callEvent(missionEvent);
                if (!missionEvent.isCanceled()) {
                        TaskService.getInstance().runTaskAsync(() -> MissionDao.getInstance().update(taskEntry));
                }
            }
        };

        r.runTask(TownyMissionInstance.getInstance());
    }
}
