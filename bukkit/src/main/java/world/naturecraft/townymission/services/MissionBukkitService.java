package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.events.DoMissionEvent;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.json.mission.MissionJson;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.UUID;

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

                    if (MissionDao.getInstance().getStartedMissions(town.getUUID()).size() == 0) {
                        return true;
                    } else {
                        ChatService.getInstance().sendMsg(playerUUID, townyMissionBukkit.getLangEntry("commands.start.onAlreadyStarted", true));
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
                    // If timed out, everyone can abort
                    MissionEntry missionEntry = MissionDao.getInstance().getStartedMissions(TownyService.getInstance().residentOf(playerUUID)).get(0);
                    if (missionEntry.isTimedout())
                        return true;

                    // If mayor, can abort any mission
                    if (TownyUtil.mayorOf(player) != null)
                        return true;

                    return entry.getStartedPlayerUUID().equals(playerUUID);
                });

        return checker.check();
    }

    public void doMission(UUID townUUID, UUID playerUUID, MissionType missionType, int amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        MissionEntry taskEntry = MissionDao.getInstance().getTownMissions(townUUID, missionEntry -> missionEntry.getMissionType().equals(missionType)).get(0);

        if (taskEntry.isCompleted() || taskEntry.isTimedout()) return;

        MissionJson json = taskEntry.getMissionJson();
        json.setCompleted(json.getCompleted() + amount);
        json.addContribution(player.getUniqueId().toString(), amount);
        taskEntry.setMissionJson(json);

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
