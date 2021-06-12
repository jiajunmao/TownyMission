/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db.yaml;

import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.components.containers.sql.SprintEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Sprint yaml.
 */
public class SprintYaml extends YamlStorage<SprintEntry> {

    /**
     * Instantiates a new Sprint yaml.
     *
     * @param instance the instance
     * @param dbType   the db type
     */
    protected SprintYaml(TownyMission instance, DbType dbType) {
        super(instance, dbType);
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
    public void update(String uuid, String townUUID, String townName, int naturePoints, int sprint, int season) {
        set(uuid + ".townUUID", townUUID);
        set(uuid + ".townName", townName);
        set(uuid + ".naturePoints", naturePoints);
        set(uuid + ".sprint", sprint);
        set(uuid + ".season", season);
    }

    @Override
    protected List<SprintEntry> getEntries() {
        List<SprintEntry> entryList = new ArrayList<>();
        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new SprintEntry(
                    UUID.fromString(file.getString(key + ".uuid")),
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
