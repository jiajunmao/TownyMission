/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.entity.SprintEntry;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.data.sql.SprintSqlStorage;
import world.naturecraft.townymission.core.data.yaml.SprintYamlStorage;

import java.util.List;
import java.util.UUID;

/**
 * The type Sprint storage.
 */
public class SprintStorage extends Storage<SprintEntry> {
    private static SprintStorage singleton;
    private final StorageType storageType;

    /**
     * Instantiates a new Sprint storage.
     *
     * @param instance the instance
     */
    public SprintStorage(TownyMission instance) {
        storageType = instance.getStorageType();
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintStorage getInstance() {
        if (singleton == null) {
            new SprintStorage((TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission"));
        }

        return singleton;
    }

    /**
     * Add.
     *
     * @param townUUID     the town uuid
     * @param townName     the town name
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    public void add(String townUUID, String townName, int naturePoints, int sprint, int season) {
        switch (storageType) {
            case YAML:
                SprintYamlStorage.getInstance().add(townUUID, townName, naturePoints, sprint, season);
                break;
            case MYSQL:
                SprintSqlStorage.getInstance().add(townUUID, townName, naturePoints, sprint, season);
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
                SprintYamlStorage.getInstance().remove(id);
                break;
            case MYSQL:
                SprintSqlStorage.getInstance().remove(id);
                break;
        }
    }

    /**
     * Update.
     *
     * @param id           the id
     * @param townUUID     the town uuid
     * @param townName     the town name
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    public void update(UUID id, String townUUID, String townName, int naturePoints, int sprint, int season) {
        switch (storageType) {
            case YAML:
                SprintYamlStorage.getInstance().update(id, townUUID, townName, naturePoints, sprint, season);
                break;
            case MYSQL:
                SprintSqlStorage.getInstance().update(id, townUUID, townName, naturePoints, sprint, season);
                break;
        }
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<SprintEntry> getEntries() {
        switch (storageType) {
            case YAML:
                return SprintYamlStorage.getInstance().getEntries();
            case MYSQL:
                return SprintSqlStorage.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }
}
