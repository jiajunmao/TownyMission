/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SprintHistoryEntry;
import world.naturecraft.townymission.components.containers.sql.SprintHistoryEntry;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.data.sql.SprintHistoryDatabase;
import world.naturecraft.townymission.data.yaml.SprintHistoryYaml;

import java.util.List;
import java.util.UUID;

/**
 * The type Sprint history storage.
 */
public class SprintHistoryStorage extends Storage<SprintHistoryEntry> {

    private static SprintHistoryStorage singleton;
    private StorageType storageType;

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
                SprintHistoryYaml.getInstance().add(season, sprint, startedTime, rankJson);
                break;
            case MYSQL:
                SprintHistoryDatabase.getInstance().add(season, sprint, startedTime, rankJson);
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
                SprintHistoryYaml.getInstance().remove(id);
                break;
            case MYSQL:
                SprintHistoryDatabase.getInstance().remove(id);
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
                SprintHistoryYaml.getInstance().update(id, season, sprint, startedTime, rankJson);
                break;
            case MYSQL:
                SprintHistoryDatabase.getInstance().update(id, season, sprint, startedTime, rankJson);
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
                return SprintHistoryYaml.getInstance().getEntries();
            case MYSQL:
                return SprintHistoryDatabase.getInstance().getEntries();
        }

        throw new IllegalStateException();
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
}
