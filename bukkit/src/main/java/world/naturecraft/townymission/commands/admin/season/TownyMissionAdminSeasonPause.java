package world.naturecraft.townymission.commands.admin.season;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
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
    public boolean playerSanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player)
                .hasPermission(new String[]{"townymission.admin.season.pause", "townymission.admin"}).check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        return new BukkitChecker(instance)
                .customCheck(() -> {
                    if (instance.getStatsConfig().getLong("season.pausedTime") != -1) {
                        commandSender.sendMessage(instance.getLangEntry("commands.pauseSeason.onAlreadyPaused"));
                        return false;
                    }

                    return true;
                })
                .customCheck(() -> {
                    // /tmsa season pause
                    if (args.length != 2 || !args[0].equalsIgnoreCase("season") || !args[1].equalsIgnoreCase("pause")) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                }).check();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (!sanityCheck(commandSender, strings)) return;

                instance.getStatsConfig().set("season.pausedTime", new Date().getTime());

                instance.getStatsConfig().save();
                commandSender.sendMessage(instance.getLangEntry("commands.pauseSeason.onSuccess"));
            }
        };

        r.runTaskAsynchronously(instance);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
