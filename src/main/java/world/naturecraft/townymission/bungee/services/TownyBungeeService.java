package world.naturecraft.townymission.bungee.services;

import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.services.PluginMessagingService;
import world.naturecraft.townymission.core.services.TownyService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Towny bungee service.
 */
public class TownyBungeeService extends TownyService {

    @Override
    public UUID residentOf(UUID playerUUID) {
        PluginMessage pluginMessage = new PluginMessage(
                playerUUID,
                "townyinfo:request",
                UUID.randomUUID(),
                1,
                new String[]{"getTownOfPlayer"});
        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(pluginMessage);

        if (response.getSize() == 0) {
            return null;
        } else {
            return UUID.fromString(response.getData()[0]);
        }
    }

    @Override
    public UUID mayorOf(UUID playerUUID) {
        PluginMessage pluginMessage = new PluginMessage(
                playerUUID,
                "townyinfo:request",
                UUID.randomUUID(),
                1,
                new String[]{"getMayorOfPlayer"});
        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(pluginMessage);

        if (response.getSize() == 0) {
            return null;
        } else {
            return UUID.fromString(response.getData()[0]);
        }
    }

    @Override
    public List<UUID> getResidents(UUID townUUID) {
        PluginMessage pluginMessage = new PluginMessage(
                null,
                "townyinfo:request",
                UUID.randomUUID(),
                1,
                new String[]{"getTownResidents", townUUID.toString()});
        // TODO: Change this!
        PluginMessage response = PluginMessagingService.getInstance().sendAndWait(pluginMessage, "nc-terra-earth");

        if (response.getSize() == 0) {
            return null;
        } else {
            List<UUID> uuidList = new ArrayList<>();
            for (String str : response.getData()) {
                uuidList.add(UUID.fromString(str));
            }
            return uuidList;
        }
    }
}
