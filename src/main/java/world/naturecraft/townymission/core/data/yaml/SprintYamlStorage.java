/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.yaml;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigLoadingException;
import world.naturecraft.townymission.core.components.entity.SprintEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.enums.ServerType;
import world.naturecraft.townymission.core.data.db.SprintStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Sprint yaml.
 */
public class SprintYamlStorage extends YamlStorage<SprintEntry> implements SprintStorage {

    private static SprintYamlStorage singleton;

    /**
     * Instantiates a new Sprint yaml.
     *
     * @param instance the instance
     * @throws ConfigLoadingException the config loading exception
     */
    public SprintYamlStorage(TownyMissionInstance instance) throws ConfigLoadingException {
        super(instance, DbType.SPRINT);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintYamlStorage getInstance() {
        if (singleton == null) {
            new SprintYamlStorage(TownyMissionInstance.getInstance());
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
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".townUUID", townUUID);
        add(uuid + ".townName", townName);
        add(uuid + ".naturePoints", naturePoints);
        add(uuid + ".sprint", sprint);
        add(uuid + ".season", season);
    }

    /**
     * Update.
     *
     * @param uuid         the uuid
     * @param townUUID     the town uuid
     * @param townName     the town name
     * @param naturePoints the nature points
     * @param sprint       the sprint
     * @param season       the season
     */
    public void update(UUID uuid, String townUUID, String townName, int naturePoints, int sprint, int season) {
        set(uuid + ".townUUID", townUUID);
        set(uuid + ".townName", townName);
        set(uuid + ".naturePoints", naturePoints);
        set(uuid + ".sprint", sprint);
        set(uuid + ".season", season);
    }

    @Override
    public List<SprintEntry> getEntries() {
        List<SprintEntry> entryList = new ArrayList<>();
        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new SprintEntry(
                    UUID.fromString(key),
                    file.getString(key + ".townUUID"),
                    file.getString(key + ".townName"),
                    file.getInt(key + ".naturePoints"),
                    file.getInt(key + ".sprint"),
                    file.getInt(key + ".season")
            ));
        }

        return entryList;
    }
}
