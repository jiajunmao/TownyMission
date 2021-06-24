/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.yaml;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.core.components.entity.SprintHistoryEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.data.db.SprintHistoryStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Sprint history yaml.
 */
public class SprintHistoryYamlStorage extends YamlStorage<SprintHistoryEntry> implements SprintHistoryStorage {

    private static SprintHistoryYamlStorage singleton;

    /**
     * Instantiates a new Sprint history yaml.
     *
     * @param instance the instance
     */
    public SprintHistoryYamlStorage(TownyMissionInstance instance) {
        super(instance, DbType.SPRINT_HISTORY);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintHistoryYamlStorage getInstance() {
        if (singleton == null) {
            new SprintHistoryYamlStorage(TownyMissionInstance.getInstance());
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
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".season", season);
        add(uuid + ".sprint", sprint);
        add(uuid + ".startedTime", startedTime);
        add(uuid + ".rankJson", rankJson);
    }

    /**
     * Update.
     *
     * @param uuid        the uuid
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(UUID uuid, int season, int sprint, long startedTime, String rankJson) {
        set(uuid + ".season", season);
        set(uuid + ".sprint", sprint);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".rankJson", rankJson);
    }

    @Override
    public List<SprintHistoryEntry> getEntries() {
        List<SprintHistoryEntry> entryList = new ArrayList<>();
        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new SprintHistoryEntry(
                    UUID.fromString(key),
                    file.getInt(key + ".season"),
                    file.getInt(key + ".sprint"),
                    file.getLong(key + ".startedTime"),
                    file.getString(key + ".rankJson")
            ));
        }

        return entryList;
    }
}