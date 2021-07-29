package world.naturecraft.townymission;

import world.naturecraft.townymission.components.enums.ServerType;

/**
 * The type Towny mission instance type.
 */
public class TownyMissionInstanceType {

    /**
     * The constant serverType.
     */
    public static ServerType serverType;

    private static TownyMissionInstance instance;

    public static void registerInstance(TownyMissionInstance instance) {
        TownyMissionInstanceType.instance = instance;
    }

    public static <T extends TownyMissionInstance> T getInstance() {
        return (T) TownyMissionInstanceType.instance;
    }

    /**
     * Is bungee boolean.
     *
     * @return the boolean
     */
    public static boolean isBungee() {
        return serverType.equals(ServerType.BUNGEE);
    }

    /**
     * Is bukkit boolean.
     *
     * @return the boolean
     */
    public static boolean isBukkit() {
        return serverType.equals(ServerType.BUKKIT);
    }

}
