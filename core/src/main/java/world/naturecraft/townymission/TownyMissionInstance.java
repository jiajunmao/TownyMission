package world.naturecraft.townymission;

import world.naturecraft.naturelib.InstanceType;
import world.naturecraft.naturelib.NaturePlugin;
import world.naturecraft.naturelib.config.NatureConfig;
import world.naturecraft.townymission.components.enums.LogLevel;

import java.util.List;

public interface TownyMissionInstance extends NaturePlugin {
    static <T extends TownyMissionInstance> T getInstance() {
        return InstanceType.getInstance();
    }

    boolean isMainServer();

    NatureConfig getStatsConfig();

    List<String> getGuiLangEntries(String path);

    String getGuiLangEntry(String path);

    int debugLevel();

    /**
     * This defaults to the INFO channel
     *
     * @param trace the trace
     */
    void log(String trace);

    /**
     * Log the trace to desired channel
     *
     * @param trace
     * @param level
     */
    void log(String trace, LogLevel level);
}
