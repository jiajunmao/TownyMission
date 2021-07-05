package world.naturecraft.townymission.commands;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.CooldownService;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.services.TimerService;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Towny mission abort.
 */
public class TownyMissionAbort extends TownyMissionCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAbort(TownyMissionBukkit instance) {
        super(instance);
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
            // /tms abort #num
            // /tms abort all
            // /tms abort -> this is WRONG

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    if (sanityCheck(player, args)) {
                        Town town = TownyUtil.residentOf(player);
                        if (Util.isInt(args[1])) {
                            MissionEntry entry = MissionDao.getInstance().getIndexedMission(town.getUUID(), Integer.parseInt(args[1]));
                            MissionService.getInstance().abortMission(player.getUniqueId(), entry);
                        } else {
                            for (MissionEntry entry : MissionDao.getInstance().getStartedMissions(town.getUUID())) {
                                MissionService.getInstance().abortMission(player.getUniqueId(), entry);
                            }
                        }
                        CooldownService.getInstance().startCooldown(town.getUUID(), Util.minuteToMs(instance.getConfig().getInt("mission.cooldown")));
                    }
                }
            };

            r.runTaskAsynchronously(instance);
        }

        return true;
    }

    /**
     * Sanity check boolean.
     *
     * @param player the player
     * @return the boolean
     */
    public boolean sanityCheck(@NotNull Player player, String[] args) {

        BukkitChecker checker = new BukkitChecker(instance).target(player)
                .hasTown()
                .hasStarted()
                .hasPermission("townymission.player")
                .customCheck(() -> {
                    if (args.length != 2 ||
                            (!Util.isInt(args[1])
                                    || !args[1].equalsIgnoreCase("all")
                                    || (Util.isInt(args[1]) && Integer.parseInt(args[1]) < 1 && Integer.parseInt(args[1]) > 15))) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    Town town = TownyUtil.residentOf(player);
                    if (TownyUtil.mayorOf(player) != null)
                        return true;

                    if (Util.isInt(args[1])) {
                        MissionEntry entry = MissionDao.getInstance().getIndexedMission(town.getUUID(), Integer.parseInt(args[1]));
                        if (entry.getStartedPlayerUUID().equals(player.getUniqueId())) {
                            return true;
                        } else {
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.abort.onNotMayorOrStarter"));
                            return false;
                        }
                    } else {
                        boolean result = true;
                        for (MissionEntry entry : MissionDao.getInstance().getStartedMissions(town.getUUID())) {
                            result = result && entry.getStartedPlayerUUID().equals(player.getUniqueId());
                        }

                        if (result) {
                            return true;
                        } else {
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.abort.onNotMayorOrStarter"));
                            return false;
                        }
                    }
                })
                .customCheck(() -> {
                    if (TimerService.getInstance().isInInterval(RankType.SEASON) || TimerService.getInstance().isInInterval(RankType.SPRINT)) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onClickDuringRecess"));
                        return false;
                    }
                    return true;
                });

        return checker.check();
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
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabList = new ArrayList<>();
        if (args.length == 2) {
            tabList.add("#num");
            tabList.add("all");
        }
        return tabList;
    }
}
