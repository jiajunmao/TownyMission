package world.naturecraft.townymission.commands.templates;

import org.bukkit.entity.Player;
import world.naturecraft.naturelib.commands.Command;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.services.ChatService;

public abstract class TownyMissionCommand extends Command {

    protected TownyMissionBukkit instance;

    public TownyMissionCommand(TownyMissionBukkit instance) {
        this(instance, "townymission", "tms");
    }

    public TownyMissionCommand(TownyMissionBukkit instance, String commandName, String commandAlias) {
        super(instance, commandName, commandAlias);
        this.instance = instance;
    }

    public void onUnknown(Player player) {
        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandNotFound"));
    }
}
