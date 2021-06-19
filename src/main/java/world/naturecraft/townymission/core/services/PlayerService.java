package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.services.PlayerBukkitService;
import world.naturecraft.townymission.bungee.services.PlayerBungeeService;

import java.util.UUID;

public abstract class PlayerService {

    public static PlayerService singleton;

    public static PlayerService getInstance() {
        if (singleton == null) {
            if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
                singleton = PlayerBukkitService.getInstance();
            } else {
                singleton = PlayerBungeeService.getInstance();
            }
        }

        return singleton;
    }

    public abstract int getNumEmptySlot(UUID playerUUID);

    public abstract void addItem(UUID playerUUID, String material, int amount);
}
