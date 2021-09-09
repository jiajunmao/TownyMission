package world.naturecraft.townymission.commands.templates;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import world.naturecraft.naturelib.commands.CommandRoot;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.services.ChatService;

public abstract class TownyMissionCommandRoot extends CommandRoot {

    protected TownyMissionBukkit instance;

    public TownyMissionCommandRoot(TownyMissionBukkit instance, int argPos, String commandName, String commandAlias) {
        super(instance, argPos, commandName, commandAlias);
        this.instance = instance;
    }

    @Override
    public void onUnknown(CommandSender sender) {
        if (sender instanceof Player) {
            ChatService.getInstance().sendMsg(((Player) sender).getUniqueId(), instance.getLangEntry("universal.onCommandNotFound", true));
        } else if (sender instanceof ConsoleCommandSender) {
            ChatService.getInstance().sendConsoleMsg(instance.getLangEntry("universal.onCommandNotFound", false));
        }
    }

    @Override
    public void onHelp(CommandSender sender) {
        if (sender instanceof Player) {
            ChatService.getInstance().sendMsg(((Player) sender).getUniqueId(), instance.getLangEntry("universal.onCommandNotFound"));
        } else if (sender instanceof ConsoleCommandSender) {
            ChatService.getInstance().sendConsoleMsg(instance.getLangEntry("universal.onCommandNotFound", false));
        }
    }
}
