package world.naturecraft.townymission;

import world.naturecraft.naturelib.InstanceType;
import world.naturecraft.naturelib.NaturePlugin;
import world.naturecraft.naturelib.config.NatureConfig;

import java.util.List;

public interface TownyMissionInstance extends NaturePlugin {
    static <T extends TownyMissionInstance> T getInstance() {
        return InstanceType.getInstance();
    }

    boolean isMainServer();

    NatureConfig getStatsConfig();

    List<String> getGuiLangEntries(String path);

    String getGuiLangEntry(String path);
}
