/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.data.sql.SeasonDatabase;
import world.naturecraft.townymission.data.yaml.SeasonYaml;

import java.util.List;
import java.util.UUID;

/**
 * The type Season storage.
 */
public class SeasonStorage extends Storage<SeasonEntry> {

    private static SeasonStorage singleton;
    private StorageType storageType;

    /**
     * Instantiates a new Season storage.
     *
     * @param instance the instance
     */
    public SeasonStorage(TownyMission instance) {
        storageType = instance.getStorageType();
        singleton = this;
    }

    /**
     * Add.
     *
     * @param townUUID    the town uuid
     * @param townName    the town name
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void add(String townUUID, String townName, int seasonPoint, int season) {
        switch (storageType) {
            case YAML:
                SeasonYaml.getInstance().add(townUUID, townName, seasonPoint, season);
                break;
            case MYSQL:
                SeasonDatabase.getInstance().add(townUUID, townName, seasonPoint, season);
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
                SeasonYaml.getInstance().remove(id);
                break;
            case MYSQL:
                SeasonDatabase.getInstance().remove(id);
                break;
        }
    }

    /**
     * Update.
     *
     * @param id          the id
     * @param townUUID    the town uuid
     * @param townName    the town name
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void update(UUID id, String townUUID, String townName, int seasonPoint, int season) {
        switch (storageType) {
            case YAML:
                SeasonYaml.getInstance().update(id, townUUID, townName, seasonPoint, season);
                break;
            case MYSQL:
                SeasonDatabase.getInstance().update(id, townUUID, townName, seasonPoint, season);
                break;
        }
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<SeasonEntry> getEntries() {
        switch (storageType) {
            case YAML:
                return SeasonYaml.getInstance().getEntries();
            case MYSQL:
                return SeasonDatabase.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonStorage getInstance() {
        if (singleton == null) {
            new SeasonStorage((TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission"));
        }

        return singleton;
    }
}
