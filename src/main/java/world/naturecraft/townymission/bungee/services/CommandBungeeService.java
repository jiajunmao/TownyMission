package world.naturecraft.townymission.bungee.services;

import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.CommandService;
import world.naturecraft.townymission.core.services.PluginMessagingService;

import java.util.UUID;

public class CommandBungeeService extends CommandService {

    private static CommandBungeeService singleton;

    public static CommandBungeeService getInstance() {
        if (singleton == null) {
            singleton = new CommandBungeeService();
        }

        return singleton;
    }

    @Override
    public void dispatchCommand(UUID playerUUID, String command) {
        PluginMessage request = new PluginMessage(
                playerUUID,
                "command:request",
                UUID.randomUUID(),
                1,
                new String[]{command}
        );

        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request);
    }
}
