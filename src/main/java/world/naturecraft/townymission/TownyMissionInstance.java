package world.naturecraft.townymission;

import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.core.components.enums.ServerType;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.config.MainConfig;
import world.naturecraft.townymission.core.config.StatsConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public interface TownyMissionInstance {

    void onEnable();

    void onDisable();

    String getLangEntry(String path);

    MainConfig getInstanceConfig();

    StatsConfig getStatsConfig();

    ServerType getServerType();

    StorageType getStorageType();

    File getInstanceDataFolder();

    InputStream getInstanceResource(String filePath);

    Logger getLogger();

    void saveInstanceResource(String filePath, boolean replace) throws IOException;

    @SuppressWarnings("unchecked")
    static <T extends TownyMissionInstance> T getInstance() {
        if (TownyMissionInstanceType.serverType.equals(ServerType.BUKKIT)) {
            return (T) Bukkit.getPluginManager().getPlugin("TownyMission");
        } else {
            return (T) ProxyServer.getInstance().getPluginManager().getPlugin("TownyMission");
        }
    }
}
