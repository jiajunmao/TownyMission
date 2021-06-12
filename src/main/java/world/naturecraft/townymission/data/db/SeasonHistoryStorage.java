/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SeasonHistoryEntry;
import world.naturecraft.townymission.components.enums.StorageType;
import world.naturecraft.townymission.data.sql.SeasonHistoryDatabase;
import world.naturecraft.townymission.data.yaml.SeasonHistoryYaml;

import java.util.List;
import java.util.UUID;

/**
 * The type Season history storage.
 */
public class SeasonHistoryStorage extends Storage<SeasonHistoryEntry> {

    private static SeasonHistoryStorage singleton;
    private StorageType storageType;

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
     * Add.
     *
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void add(int season, long startedTime, String rankJson) {
        switch (storageType) {
            case YAML:
                SeasonHistoryYaml.getInstance().add(season, startedTime, rankJson);
                break;
            case MYSQL:
                SeasonHistoryDatabase.getInstance().add(season, startedTime, rankJson);
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
                SeasonHistoryYaml.getInstance().remove(id);
                break;
            case MYSQL:
                SeasonHistoryDatabase.getInstance().remove(id);
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
                SeasonHistoryYaml.getInstance().update(id, season, startedTime, rankJson);
                break;
            case MYSQL:
                SeasonHistoryDatabase.getInstance().update(id, season, startedTime, rankJson);
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
                return SeasonHistoryYaml.getInstance().getEntries();
            case MYSQL:
                return SeasonHistoryDatabase.getInstance().getEntries();
        }

        throw new IllegalStateException();
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
}
