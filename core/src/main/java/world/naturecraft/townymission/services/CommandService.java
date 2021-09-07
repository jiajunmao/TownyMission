package world.naturecraft.townymission.services;

import world.naturecraft.naturelib.InstanceType;

import java.util.UUID;

/**
 * The type Command service.
 */
public abstract class CommandService extends TownyMissionService {

    private static CommandService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CommandService getInstance() {
        if (singleton == null) {
            if (InstanceType.isBukkit()) {
                String packageName = CommandService.class.getPackage().getName();
                try {
                    singleton = (CommandService) Class.forName(packageName + "." + "CommandBukkitService").newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return singleton;
    }

    /**
     * Dispatch command.
     *
     * @param playerUUID the player uuid
     * @param command    the command
     */
    public abstract void dispatchCommand(UUID playerUUID, String command);

    public abstract void dispatchConsoleCommand(String command);
}
