package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Towny bukkit service.
 */
public class TownyBukkitService extends TownyService {

    private static TownyBukkitService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TownyBukkitService getInstance() {
        if (singleton == null) {
            singleton = new TownyBukkitService();
        }

        return singleton;
    }

    @Override
    public UUID residentOf(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return TownyUtil.residentOf(player) == null ? null : TownyUtil.residentOf(player).getUUID();
    }

    @Override
    public UUID mayorOf(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return TownyUtil.mayorOf(player) == null ? null : TownyUtil.mayorOf(player).getUUID();
    }

    @Override
    public List<UUID> getResidents(UUID townUUID) {
        try {
            List<Resident> players = TownyAPI.getInstance().getDataSource().getTown(townUUID).getResidents();
            List<UUID> playerList = new ArrayList<>();
            for (Resident resident : players) {
                playerList.add(resident.getUUID());
            }

            return playerList;
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    @Override
    public Integer getNumResidents(UUID townUUID) {
        try {
            return TownyAPI.getInstance().getDataSource().getTown(townUUID).getNumResidents();
        } catch (NotRegisteredException e) {
            return null;
        }
    }
}
