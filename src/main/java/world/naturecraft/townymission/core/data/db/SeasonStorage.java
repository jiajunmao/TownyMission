/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.entity.SeasonEntry;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.data.sql.SeasonSqlStorage;
import world.naturecraft.townymission.core.data.yaml.SeasonYamlStorage;

import java.util.List;
import java.util.UUID;

/**
 * The type Season storage.
 */
public class SeasonStorage extends Storage<SeasonEntry> {

    private static SeasonStorage singleton;
    private final StorageType storageType;

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
                SeasonYamlStorage.getInstance().add(townUUID, townName, seasonPoint, season);
                break;
            case MYSQL:
                SeasonSqlStorage.getInstance().add(townUUID, townName, seasonPoint, season);
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
                SeasonYamlStorage.getInstance().remove(id);
                break;
            case MYSQL:
                SeasonSqlStorage.getInstance().remove(id);
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
                SeasonYamlStorage.getInstance().update(id, townUUID, townName, seasonPoint, season);
                break;
            case MYSQL:
                SeasonSqlStorage.getInstance().update(id, townUUID, townName, seasonPoint, season);
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
                return SeasonYamlStorage.getInstance().getEntries();
            case MYSQL:
                return SeasonSqlStorage.getInstance().getEntries();
        }

        throw new IllegalStateException();
    }
}
