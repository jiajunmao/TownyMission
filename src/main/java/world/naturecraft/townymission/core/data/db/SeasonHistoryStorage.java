/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.data.sql.SeasonHistorySqlStorage;
import world.naturecraft.townymission.core.data.yaml.SeasonHistoryYamlStorage;

import java.util.List;
import java.util.UUID;

/**
 * The type Season history storage.
 */
public class SeasonHistoryStorage extends Storage<SeasonHistoryEntry> {

    private static SeasonHistoryStorage singleton;
    private final StorageType storageType;

    /**
     * Instantiates a new Season history storage.
     *
     * @param instance the instance
     */
    public SeasonHistoryStorage(TownyMission instance) {
        storageType = instance.getStorageType();
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonHistoryStorage getInstance() {
        if (singleton == null) {
            new SeasonHistoryStorage((TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission"));
        }

        return singleton;
    }

    /**
     * Add.
     *
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void add(int season, long startedTime, String rankJson) {
        switch (storageType) {
            case YAML:
                SeasonHistoryYamlStorage.getInstance().add(season, startedTime, rankJson);
                break;
            case MYSQL:
                SeasonHistorySqlStorage.getInstance().add(season, startedTime, rankJson);
                break;
        }
    }

    /**
     * Remove.
     *
     * @param id the id
     */
    public void remove(UUID id) {
        switch (storageType) {
            case YAML:
                SeasonHistoryYamlStorage.getInstance().remove(id);
                break;
            case MYSQL:
                SeasonHistorySqlStorage.getInstance().remove(id);
                break;
        }
    }

    /**
     * Update.
     *
     * @param id          the id
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(UUID id, int season, long startedTime, String rankJson) {
        switch (storageType) {
            case YAML:
                SeasonHistoryYamlStorage.getInstance().update(id, season, startedTime, rankJson);
                break;
            case MYSQL:
                SeasonHistorySqlStorage.getInstance().update(id, season, startedTime, rankJson);
                break;
        }
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<SeasonHistoryEntry> getEntries() {
        switch (storageType) {
            case YAML:
                return SeasonHistoryYamlStorage.getInstance().getEntries();
            case MYSQL:
                return SeasonHistorySqlStorage.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }
}
