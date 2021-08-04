package world.naturecraft.townymission.commands;

import org.bukkit.entity.Player;
import world.naturecraft.naturelib.NaturePlugin;
import world.naturecraft.naturelib.commands.Command;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.services.ChatService;

public abstract class TownyMissionCommand extends Command {

    protected TownyMissionBukkit instance;

    public TownyMissionCommand(TownyMissionBukkit instance) {
        super((NaturePlugin) instance);
        this.instance = instance;
    }

    public void onUnknown(Player player) {
        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandNotFound"));
    }
}
