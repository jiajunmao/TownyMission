package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.services.TownyBukkitService;
import world.naturecraft.townymission.bungee.services.TownyBungeeService;

import java.util.List;
import java.util.UUID;

public abstract class TownyService {

    private static TownyService singleton;

    public static TownyService getInstance() {
        if (singleton == null) {
            if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
                singleton = TownyBukkitService.getInstance();
            } else {
                singleton = TownyBungeeService.getInstance();
            }
        }

        return singleton;
    }

    public abstract UUID residentOf(UUID playerUUID);

    public abstract UUID mayorOf(UUID playerUUID);

    public abstract List<UUID> getResidents(UUID townUUID);
}
