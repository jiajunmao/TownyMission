package world.naturecraft.townymission.commands.admin.sprint;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionAdminCommand;
import world.naturecraft.townymission.components.entity.SprintEntry;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.core.RankingService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TownyMissionAdminSprintRank extends TownyMissionAdminCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminSprintRank(TownyMissionBukkit instance) {
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
        // /tmsa sprint rank <town>
        BukkitChecker checker = new BukkitChecker(instance).target(player).silent(false)
                .hasPermission(new String[]{"townymission.admin.sprint.rank", "townymission.admin"});


        return checker.check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        BukkitChecker checker = new BukkitChecker(instance)
                .customCheck(() -> {
                    if (!args[0].equalsIgnoreCase("sprint") || !args[1].equalsIgnoreCase("rank")) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    String townName = args[2];
                    if (TownyUtil.getTown(townName) == null) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onTownNameInvalid"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    String townName = args[2];
                    UUID townUUID = TownyUtil.getTown(townName).getUUID();
                    SprintEntry sprintEntry = SprintDao.getInstance().get(townUUID);
                    if (sprintEntry == null) {
                        sprintEntry = new SprintEntry(UUID.randomUUID(), townUUID, 0, instance.getStatsConfig().getInt("sprint.current"), instance.getStatsConfig().getInt("season.current"));
                        SprintDao.getInstance().add(sprintEntry);
                    }
                    return true;
                });

        return checker.check();
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

                // /tmsa sprint rank <town>
                Town town = TownyUtil.getTown(args[2]);
                int rank = RankingService.getInstance().getRank(town.getUUID(), RankType.SPRINT);

                String display = "&e" + town.getName() + " &fis ranked &3" + rank + " &fin the current sprint";
                sender.sendMessage(display);
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
        // /tmsa sprint rank <town>
        List<String> tabList = new ArrayList<>();
        if (args.length == 3) {
            for (Town town : TownyUtil.getTowns()) {
                tabList.add(town.getName());
            }
        }

        return tabList;
    }
}
