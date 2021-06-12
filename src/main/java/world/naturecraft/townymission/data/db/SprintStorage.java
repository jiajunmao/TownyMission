/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SprintEntry;
import world.naturecraft.townymission.components.containers.sql.SprintEntry;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.data.sql.SprintDatabase;
import world.naturecraft.townymission.data.yaml.SprintYaml;

import java.util.List;
import java.util.UUID;

/**
 * The type Sprint storage.
 */
public class SprintStorage extends Storage<SprintEntry> {
    private static SprintStorage singleton;
    private StorageType storageType;

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
                SprintYaml.getInstance().add(townUUID, townName, naturePoints, sprint, season);
                break;
            case MYSQL:
                SprintDatabase.getInstance().add(townUUID, townName, naturePoints, sprint, season);
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
                SprintYaml.getInstance().remove(id);
                break;
            case MYSQL:
                SprintDatabase.getInstance().remove(id);
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
                SprintYaml.getInstance().update(id, townUUID, townName, naturePoints, sprint, season);
                break;
            case MYSQL:
                SprintDatabase.getInstance().update(id, townUUID, townName, naturePoints, sprint, season);
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
                return SprintYaml.getInstance().getEntries();
            case MYSQL:
                return SprintDatabase.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public SprintStorage getInstance() {
        if (singleton == null) {
            new SprintStorage((TownyMission) Bukkit.getPluginManager().getPlugin("TownySprint"));
        }

        return singleton;
    }
}
