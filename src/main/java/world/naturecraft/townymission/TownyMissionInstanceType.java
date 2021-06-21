package world.naturecraft.townymission;

import world.naturecraft.townymission.core.components.enums.ServerType;

/**
 * The type Towny mission instance type.
 */
public class TownyMissionInstanceType {

    /**
     * The constant serverType.
     */
    public static ServerType serverType;

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
