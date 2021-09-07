package world.naturecraft.townymission.commands.admin.season;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionAdminCommand;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.utils.BukkitChecker;

import java.util.Date;
import java.util.List;

public class TownyMissionAdminSeasonPause extends TownyMissionAdminCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminSeasonPause(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .hasPermission(new String[]{"townymission.admin.season.pause", "townymission.admin"})
                .customCheck(() -> {
                    if (((TownyMissionBukkit) instance).getStatsConfig().getLong("season.pausedTime") != -1) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.pauseSeason.onAlreadyPaused"));
                        return false;
                    }

                    return true;
                })
                .customCheck(() -> {
                    // /tmsa season pause
                    if (args.length != 2 || !args[0].equalsIgnoreCase("season") || !args[1].equalsIgnoreCase("pause")) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                }).check();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!sanityCheck(player, strings)) return false;

            ((TownyMissionBukkit) instance).getStatsConfig().set("season.pausedTime", new Date().getTime());

            ((TownyMissionBukkit) instance).getStatsConfig().save();
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
