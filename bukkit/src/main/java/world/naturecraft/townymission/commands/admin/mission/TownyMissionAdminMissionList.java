/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands.admin.mission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.naturelib.utils.MultilineBuilder;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionAdminCommand;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.json.mission.MissionJson;
import world.naturecraft.townymission.config.MissionConfigParser;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.utils.BukkitChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * The type Towny mission list all.
 */
public class TownyMissionAdminMissionList extends TownyMissionAdminCommand {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminMissionList(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean playerSanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .hasPermission(new String[]{"townymission.admin.mission.list", "townymission.admin"}).check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        return new BukkitChecker(instance)
                .customCheck(() -> {
                    // /tmsa mission list type
                    if (args.length == 3) {
                        for (MissionType missionType : MissionType.values()) {
                            if (args[2].equalsIgnoreCase(missionType.name())) {
                                return true;
                            }
                        }
                    }

                    commandSender.sendMessage(instance.getLangEntry("universal.onCommandFormatError", true));
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
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (!sanityCheck(sender, args)) return;

                MultilineBuilder builder = new MultilineBuilder("&e------TownyMission Missions------&7");

                MissionType missionType = MissionType.valueOf(args[2].toUpperCase(Locale.ROOT));
                if (instance.isMissionEnabled(missionType)) {
                    Collection<MissionJson> collection = MissionConfigParser.parse(missionType, instance);
                    builder.add("&eMission Type&f: " + missionType.name());
                    builder.add(" ");
                    for (MissionJson json : collection) {
                        builder.add(" " + json.getDisplayLine());
                    }

                    sender.sendMessage(builder.toString());
                } else {
                    sender.sendMessage(instance.getLangEntry("commands.listMission.onNotEnabled"));
                }
            }
        };

        r.runTaskAsynchronously(instance);
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

        // /tmsa mission list <#type>
        if (args.length == 3) {
            for (MissionType e : MissionType.values()) {
                if (e.name().contains(args[2])) {
                    tabList.add(e.name());
                }
            }
        }

        return tabList;
    }
}
