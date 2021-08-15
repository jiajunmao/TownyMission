package world.naturecraft.townymission.commands.admin.sprint;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionCommandRoot;
import world.naturecraft.townymission.services.ChatService;

import java.util.List;

public class TownyMissionAdminSprintRoot extends TownyMissionCommandRoot {

    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionAdminSprintRoot(TownyMissionBukkit instance) {
        super(instance, 1, "townymissionadmin", "tmsa");
    }

    /**
     * Sanity check boolean.
     *
     * @param player the player
     * @param args   the args
     * @return the boolean
     */
    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return false;
    }

    @Override
    public void onUnknown(Player player) {
        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandNotFound"));
    }

    @Override
    public void onHelp(Player player) {
        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandNotFound"));
    }

    @Override
    protected void postProcessTabList(CommandSender commandSender, List<String> list) {
        // Do nothing
    }
}
