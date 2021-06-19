/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.yaml;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.core.components.entity.SeasonEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.data.db.SeasonStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Season yaml.
 */
public class SeasonYamlStorage extends YamlStorage<SeasonEntry> implements SeasonStorage {

    private static SeasonYamlStorage singleton;

    /**
     * Instantiates a new Season yaml.
     *
     * @param instance the instance
     */
    public SeasonYamlStorage(TownyMissionInstance instance) {
        super(instance, DbType.SEASON);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonYamlStorage getInstance() {
        if (singleton == null) {
            new SeasonYamlStorage(TownyMissionInstance.getInstance());
        }
        return singleton;
    }

    /**
     * Add.
     *
     * @param townUUID    the town uuid
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void add(UUID townUUID, int seasonPoint, int season) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".townUUID", townUUID);
        add(uuid + ".seasonPoint", seasonPoint);
        add(uuid + ".season", season);
    }

    /**
     * Update.
     *
     * @param uuid        the uuid
     * @param townUUID    the town uuid
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void update(UUID uuid, UUID townUUID, int seasonPoint, int season) {
        set(uuid + ".townUUID", townUUID);
        set(uuid + ".seasonPoint", seasonPoint);
        set(uuid + ".season", season);
    }

    @Override
    public List<SeasonEntry> getEntries() {
        List<SeasonEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new SeasonEntry(
                    UUID.fromString(key),
                    UUID.fromString(file.getString(key + ".townUUID")),
                    file.getInt(key + ".seasonPoint"),
                    file.getInt(key + ".season")
            ));
        }

        return entryList;
    }
}
