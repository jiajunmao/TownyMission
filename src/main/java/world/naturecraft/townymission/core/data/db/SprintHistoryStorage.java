/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.entity.SprintHistoryEntry;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.data.sql.SprintHistorySqlStorage;
import world.naturecraft.townymission.core.data.yaml.SprintHistoryYamlStorage;

import java.util.List;
import java.util.UUID;

/**
 * The type Sprint history storage.
 */
public class SprintHistoryStorage extends Storage<SprintHistoryEntry> {

    private static SprintHistoryStorage singleton;
    private final StorageType storageType;

    /**
     * Instantiates a new Sprint history storage.
     *
     * @param instance the instance
     */
    public SprintHistoryStorage(TownyMission instance) {
        storageType = instance.getStorageType();
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintHistoryStorage getInstance() {
        if (singleton == null) {
            new SprintHistoryStorage((TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission"));
        }

        return singleton;
    }

    /**
     * Add.
     *
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void add(int season, int sprint, long startedTime, String rankJson) {
        switch (storageType) {
            case YAML:
                SprintHistoryYamlStorage.getInstance().add(season, sprint, startedTime, rankJson);
                break;
            case MYSQL:
                SprintHistorySqlStorage.getInstance().add(season, sprint, startedTime, rankJson);
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
                SprintHistoryYamlStorage.getInstance().remove(id);
                break;
            case MYSQL:
                SprintHistorySqlStorage.getInstance().remove(id);
                break;
        }
    }

    /**
     * Update.
     *
     * @param id          the id
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(UUID id, int season, int sprint, long startedTime, String rankJson) {
        switch (storageType) {
            case YAML:
                SprintHistoryYamlStorage.getInstance().update(id, season, sprint, startedTime, rankJson);
                break;
            case MYSQL:
                SprintHistorySqlStorage.getInstance().update(id, season, sprint, startedTime, rankJson);
                break;
        }
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<SprintHistoryEntry> getEntries() {
        switch (storageType) {
            case YAML:
                return SprintHistoryYamlStorage.getInstance().getEntries();
            case MYSQL:
                return SprintHistorySqlStorage.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }
}
