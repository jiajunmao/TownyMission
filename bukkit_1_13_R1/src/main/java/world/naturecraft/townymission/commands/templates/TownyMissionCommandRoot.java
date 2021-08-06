package world.naturecraft.townymission.commands.templates;

import org.bukkit.entity.Player;
import world.naturecraft.naturelib.NaturePlugin;
import world.naturecraft.naturelib.commands.CommandRoot;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.services.ChatService;

public abstract class TownyMissionCommandRoot extends CommandRoot {

    protected TownyMissionBukkit instance;

    public TownyMissionCommandRoot(TownyMissionBukkit instance, int argPos) {
        super(instance, argPos, "townymission", "tms");
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
