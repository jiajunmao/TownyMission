/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.data.yaml.*;
import world.naturecraft.townymission.core.services.ChatService;

import java.util.List;

/**
 * The type Towny mission reload.
 */
public class TownyMissionAdminReload extends TownyMissionAdminCommand {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminReload(TownyMissionBukkit instance) {
        super(instance);
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
        return new BukkitChecker(instance).target(player)
                .customCheck(() -> new BukkitChecker(instance).target(player).hasPermission("tms.admin").check()
                        || new BukkitChecker(instance).target(player).hasPermission("tms.commands.reload").check())
                .customCheck(() -> {
                    // /tms admin reload
                    if (args.length == 2 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("reload"))
                        return true;

                    ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandFormatError"));
                    return false;
                }).check();
    }

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /tms reload
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!sanityCheck(player, args)) return false;

            try {
                instance.reloadConfigs();

                if (instance.getStorageType().equals(StorageType.YAML)) {
                    new CooldownYamlStorage(instance);
                    new MissionHistoryYamlStorage(instance);
                    new MissionYamlStorage(instance);
                    new SeasonYamlStorage(instance);
                    new SeasonHistoryYamlStorage(instance);
                    new SprintYamlStorage(instance);
                    new SprintHistoryYamlStorage(instance);
                }
            } catch (ConfigLoadingException e) {
                e.printStackTrace();
                ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.reload.onFailure"));
            }

            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.reload.onSuccess"));
        }

        return true;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
