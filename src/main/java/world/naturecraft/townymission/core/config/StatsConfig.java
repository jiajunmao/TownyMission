package world.naturecraft.townymission.core.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigSavingException;
import world.naturecraft.townymission.bukkit.api.exceptions.DataProcessException;
import world.naturecraft.townymission.core.components.enums.RankType;

import java.io.File;
import java.io.IOException;

public class StatsConfig {

    private final TownyMissionInstance instance;
    private File customConfig;
    private FileConfiguration customFileConfig;

    public StatsConfig() throws ConfigLoadingException {
        this.instance = TownyMissionInstance.getInstance();
        try {
            createStatsConfig();
        } catch (IOException | InvalidConfigurationException e) {
            throw new ConfigLoadingException(e);
        }
    }

    public void createStatsConfig() throws IOException, InvalidConfigurationException {
        String fileName = "stats.yml";
        String filePath = "datastore" + File.separator + fileName;
        customConfig = new File(instance.getInstanceDataFolder(), filePath);
        if (!customConfig.exists()) {
            customConfig.getParentFile().getParentFile().mkdirs();
            customConfig.getParentFile().mkdirs();
            instance.saveInstanceResource(filePath, false);
        }

        customFileConfig = new YamlConfiguration();
        customFileConfig.load(customConfig);
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
            throw new ConfigSavingException(e);
        }
    }
}
