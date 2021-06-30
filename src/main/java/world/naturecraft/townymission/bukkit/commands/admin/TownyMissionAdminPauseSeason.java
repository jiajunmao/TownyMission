package world.naturecraft.townymission.bukkit.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigSavingException;
import world.naturecraft.townymission.bukkit.commands.TownyMissionCommand;
import world.naturecraft.townymission.bukkit.utils.BukkitChecker;
import world.naturecraft.townymission.core.services.ChatService;

import java.util.Date;
import java.util.List;

public class TownyMissionAdminPauseSeason extends TownyMissionAdminCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminPauseSeason(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .customCheck(() -> {
                    return new BukkitChecker(instance).target(player).hasPermission("townymission.admin").check()
                            || new BukkitChecker(instance).target(player).hasPermission("townymission.commands.admin.pauseSeason").check();
                })
                .customCheck(() -> {
                    if (instance.getStatsConfig().getLong("season.pausedTime") != -1) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.pauseSeason.onAlreadyPaused"));
                        return false;
                    }

                    return true;
                })
                .customCheck(() -> {
                    // /tms admin startSeason
                    return (args.length == 2 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("pauseSeason"));
                }).check();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (!sanityCheck(player, strings)) return false;

            instance.getStatsConfig().set("season.pausedTime", new Date().getTime());

            instance.getStatsConfig().save();
            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.pauseSeason.onSuccess"));
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
