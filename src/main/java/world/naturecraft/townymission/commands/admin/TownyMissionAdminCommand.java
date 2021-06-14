/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands.admin;

import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.commands.TownyMissionCommand;

/**
 * The type Towny mission admin command.
 */
public abstract class TownyMissionAdminCommand extends TownyMissionCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminCommand(TownyMission instance) {
        super(instance);
    }

}
