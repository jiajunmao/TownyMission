package world.naturecraft.townymission.bukkit.services;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.core.services.TownyService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Towny bukkit service.
 */
public class TownyBukkitService extends TownyService {

    @Override
    public UUID residentOf(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return TownyUtil.residentOf(player).getUUID();
    }

    @Override
    public UUID mayorOf(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return TownyUtil.mayorOf(player).getUUID();
    }

    @Override
    public List<UUID> getResidents(UUID townUUID) {
        try {
            List<Resident> players = TownyAPI.getInstance().getDataSource().getTown(townUUID).getResidents();
            List<UUID> playerUUID = new ArrayList<>();
            for (Resident resident : players) {
                playerUUID.add(resident.getUUID());
            }

            return playerUUID;
        } catch (NotRegisteredException e) {
            return null;
        }
    }
}
