package world.naturecraft.townymission.commands.templates;

import world.naturecraft.townymission.TownyMissionBukkit;

public abstract class TownyMissionAdminCommand extends TownyMissionCommand {

    public TownyMissionAdminCommand(TownyMissionBukkit instance) {
        super(instance, "townymissionadmin", "tmsa");
    }
}
