package world.naturecraft.townymission.commands.admin.season;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionAdminCommand;
import world.naturecraft.townymission.components.entity.SeasonEntry;
import world.naturecraft.townymission.data.dao.SeasonDao;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TownyMissionAdminSeasonPoint extends TownyMissionAdminCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminSeasonPoint(TownyMissionBukkit instance) {
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
    public boolean playerSanityCheck(@NotNull Player player, @NotNull String[] args) {
        // /tmsa season point <a/r/s> <town> <#num>
        BukkitChecker bukkitChecker = new BukkitChecker(instance).target(player).silent(false)
                .hasPermission(new String[]{"townymission.admin.sesason.point", "townymission.admin"});

        return bukkitChecker.check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        return new BukkitChecker(instance)
                .customCheck(() -> {
                    // /tmsa season point <add/set/remove> <town> <num>
                    if (args.length != 5
                            || !args[0].equalsIgnoreCase("season")
                            || !args[1].equalsIgnoreCase("point")
                            || !(args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("set") || args[2].equalsIgnoreCase("remove")
                            || !Util.isInt(args[4]))) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    String townName = args[3];
                    if (TownyUtil.getTown(townName) == null) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onTownNameInvalid"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    String townName = args[3];
                    UUID townUUID = TownyUtil.getTown(townName).getUUID();
                    SeasonEntry seasonEntry = SeasonDao.getInstance().get(townUUID);
                    if (seasonEntry == null) {
                        seasonEntry = new SeasonEntry(UUID.randomUUID(), townUUID, 0, instance.getStatsConfig().getInt("season.current"));
                        SeasonDao.getInstance().add(seasonEntry);
                    }
                    return true;
                }).check();
    }

    /**
     * Executes the given command, returning its success
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

            // /tmsa season point <add/set/remove> <town> <num>
            String action = args[2].toLowerCase(Locale.ROOT);
            Town targetTown = TownyUtil.getTown(args[3]);
            SeasonEntry seasonEntry = SeasonDao.getInstance().get(targetTown.getUUID());
            assert seasonEntry != null;
            int amount = Integer.parseInt(args[4]);
            String finalAction = "";

            switch (action) {
                case "add":
                    seasonEntry.setSeasonPoint(seasonEntry.getSeasonPoint() + amount);
                    finalAction = "added";
                    break;
                case "remove":
                    seasonEntry.setSeasonPoint(seasonEntry.getSeasonPoint() - amount);
                    finalAction = "removed";
                    break;
                case "set":
                    seasonEntry.setSeasonPoint(amount);
                    finalAction = "set";
                    break;
            }

            SeasonDao.getInstance().update(seasonEntry);
            sender.sendMessage(instance.getLangEntry("adminCommands.season_point.onSuccess")
                    .replace("%action%", finalAction)
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%town%", targetTown.getName()));
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
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        // /tmsa season point <add/set/remove> <town> <num>
        List<String> tabList = new ArrayList<>();
        if (args.length == 3) {
            tabList.add("add");
            tabList.add("set");
            tabList.add("remove");
        } else if (args.length == 4) {
            for (Town town : TownyUtil.getTowns()) {
                tabList.add(town.getName());
            }
        } else if (args.length == 5) {
            tabList.add("#num");
        }
        return tabList;
    }
}
