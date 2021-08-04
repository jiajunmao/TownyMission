package world.naturecraft.townymission;

import world.naturecraft.naturelib.InstanceType;
import world.naturecraft.naturelib.NaturePlugin;
import world.naturecraft.naturelib.config.NatureConfig;

public interface TownyMissionInstance extends NaturePlugin {
    static <T extends TownyMissionInstance> T getInstance() {
        return InstanceType.getInstance();
    }

    boolean isMainServer();

    NatureConfig getStatsConfig();
}
