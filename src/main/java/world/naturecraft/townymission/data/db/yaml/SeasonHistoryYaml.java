/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db.yaml;

import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.components.containers.sql.SeasonHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Season history yaml.
 */
public class SeasonHistoryYaml extends YamlStorage<SeasonHistoryEntry> {

    /**
     * Instantiates a new Season history yaml.
     *
     * @param instance the instance
     * @param dbType   the db type
     */
    protected SeasonHistoryYaml(TownyMission instance, DbType dbType) {
        super(instance, dbType);
    }

    /**
     * Add.
     *
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void add(int season, long startedTime, String rankJson) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".season", season);
        add(uuid + ".startedTime", startedTime);
        add(uuid + ".rankJson", rankJson);
    }

    /**
     * Update.
     *
     * @param uuid        the uuid
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(String uuid, int season, long startedTime, String rankJson) {
        set(uuid + ".season", season);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".rankJson", rankJson);
    }

    @Override
    protected List<SeasonHistoryEntry> getEntries() {
        List<SeasonHistoryEntry> entryList = new ArrayList<>();
        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new SeasonHistoryEntry(
                    UUID.fromString(file.getString(key + ".uuid")),
                    file.getInt(key + ".season"),
                    file.getLong(key + ".startedTime"),
                    file.getString(key + ".rankJson")
            ));
        }

        return entryList;
    }
}
