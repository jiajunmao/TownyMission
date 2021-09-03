/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionCommandRoot;

import java.util.List;

/**
 * The type Towny mission admin root.
 */
public class TownyMissionAdminRoot extends TownyMissionCommandRoot {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminRoot(TownyMissionBukkit instance) {
        super(instance, 0, "townymissionadmin", "tmsa");
    }

    /**
     * Sanity check boolean.
     *
     * @param player the player
     * @param args   the args
     * @return the boolean
     */
    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return false;
    }

    @Override
    protected void postProcessTabList(CommandSender commandSender, List<String> list) {
        // Do nothing
    }
}
