package world.naturecraft.townymission.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.DataProcessException;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.services.TownyMissionService;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class StatsConfigLoader {

    private final TownyMission instance;
    private File customConfig;
    private FileConfiguration customFileConfig;

    public StatsConfigLoader(TownyMission instance) {
        this.instance = instance;
        createStatsConfig();
    }

    public void createStatsConfig() {
        String fileName = "stats.yml";
        String filePath = "datastore" + File.separator + fileName;
        customConfig = new File(instance.getDataFolder(), filePath);
        if (!customConfig.exists()) {
            customConfig.getParentFile().getParentFile().mkdirs();
            customConfig.getParentFile().mkdirs();
            instance.saveResource(filePath, false);
        }

        customFileConfig = new YamlConfiguration();
        try {
            customFileConfig.load(customConfig);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public int getCurrent(RankType rankType) {
        switch (rankType) {
            case SEASON:
                return customFileConfig.getInt("season.current");
            case SPRINT:
                return customFileConfig.getInt("sprint.current");
            default:
                throw new IllegalStateException();
        }
    }

    public long getSeasonStartedTime() {
        return customFileConfig.getLong("season.startedTime");
    }

    public int getInt(String path) {
        return customFileConfig.getInt(path);
    }

    public void set(String path, Object content) {
        customFileConfig.set(path, content);
    }

    public long getLong(String path) {
        return customFileConfig.getLong(path);
    }

    public void save() {
        try {
            customFileConfig.save(customConfig);
        } catch (IOException e) {
            throw new DataProcessException(e);
        }
    }
}
