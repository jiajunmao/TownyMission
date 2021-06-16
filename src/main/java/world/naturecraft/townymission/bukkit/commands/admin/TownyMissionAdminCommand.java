/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.commands.admin;

import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.bukkit.commands.TownyMissionCommand;

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
