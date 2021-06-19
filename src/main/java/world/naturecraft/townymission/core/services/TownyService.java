package world.naturecraft.townymission.core.services;

import java.util.List;
import java.util.UUID;

public abstract class TownyService {

    private static TownyService singleton;

    public abstract UUID residentOf(UUID playerUUID);

    public abstract UUID mayorOf(UUID playerUUID);

    public abstract List<UUID> getResidents(UUID townUUID);
}
