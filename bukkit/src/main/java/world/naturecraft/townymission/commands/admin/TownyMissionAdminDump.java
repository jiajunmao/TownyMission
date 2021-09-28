package world.naturecraft.townymission.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionAdminCommand;

import java.util.List;

public class TownyMissionAdminDump extends TownyMissionAdminCommand {

    public TownyMissionAdminDump(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean playerSanityCheck(Player player, String[] strings) {
        return false;
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] strings) {
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
