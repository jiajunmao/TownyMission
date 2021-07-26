/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands.admin;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.CommandSender;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.TownyMissionCommand;
import world.naturecraft.townymission.components.entity.SeasonEntry;
import world.naturecraft.townymission.data.dao.SeasonDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.UUID;

/**
 * The type Towny mission admin command.
 */
public abstract class TownyMissionAdminCommand extends TownyMissionCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminCommand(TownyMissionBukkit instance) {
        super(instance);
    }
}
