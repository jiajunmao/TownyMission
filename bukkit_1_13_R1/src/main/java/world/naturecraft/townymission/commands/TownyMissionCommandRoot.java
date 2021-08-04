package world.naturecraft.townymission.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import world.naturecraft.naturelib.NaturePlugin;
import world.naturecraft.naturelib.commands.CommandRoot;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.services.ChatService;

import java.util.List;

public abstract class TownyMissionCommandRoot extends CommandRoot {

    protected TownyMissionBukkit instance;

    public TownyMissionCommandRoot(TownyMissionBukkit instance, int argPos) {
        super((NaturePlugin) instance, argPos);
        this.instance = instance;
    }

    @Override
    public void onUnknown(Player player) {
        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandNotFound"));
    }

    @Override
    public void onHelp(Player player) {
        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandNotFound"));
    }
}
