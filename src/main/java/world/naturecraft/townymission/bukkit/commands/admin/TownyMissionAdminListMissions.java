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
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.MissionJson;
import world.naturecraft.townymission.core.config.mission.MissionConfigParser;
import world.naturecraft.townymission.core.services.ChatService;
import world.naturecraft.townymission.core.utils.MultilineBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * The type Towny mission list all.
 */
public class TownyMissionAdminListMissions extends TownyMissionAdminCommand {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminListMissions(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .customCheck(() -> {
                    return new BukkitChecker(instance).target(player).hasPermission("townymission.admin").check()
                            || new BukkitChecker(instance).target(player).hasPermission("townymission.commands.listall").check();
                }).customCheck(() -> {
                    // /tms admin listMission #type
                    if (args.length == 3) {
                        for (MissionType missionType : MissionType.values()) {
                            if (args[2].equalsIgnoreCase(missionType.name())) {
                                return true;
                            }
                        }
                    }

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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (sanityCheck(player, args)) {
                MultilineBuilder builder = new MultilineBuilder("&e------TownyMission Missions------&7");

                MissionType missionType = MissionType.valueOf(args[2].toUpperCase(Locale.ROOT));
                Collection<MissionJson> collection = MissionConfigParser.parse(missionType, instance);
                builder.add("&eMission Type&f: " + missionType.name());
                builder.add(" ");
                for (MissionJson json : collection) {
                    builder.add(" " + json.getDisplayLine());
                }

                ChatService.getInstance().sendMsg(player.getUniqueId(), ChatService.getInstance().translateColor(builder.toString()));
            }
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
        List<String> tabList = new ArrayList<>();

        if (args.length == 3) {
            if (!args[2].equals("")) {
                for (MissionType e : MissionType.values()) {
                    if (e.name().contains(args[2].toUpperCase(Locale.ROOT))) {
                        tabList.add(e.name().toUpperCase(Locale.ROOT));
                    }
                }
            } else {
                for (MissionType e : MissionType.values()) {
                    tabList.add(e.name().toUpperCase(Locale.ROOT));
                }
            }
        }

        return tabList;
    }
}
