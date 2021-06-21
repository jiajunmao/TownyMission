package world.naturecraft.townymission.bukkit.services;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.core.components.entity.MissionEntry;
import world.naturecraft.townymission.core.data.dao.MissionDao;
import world.naturecraft.townymission.core.services.ChatService;
import world.naturecraft.townymission.core.services.MissionService;

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


}
