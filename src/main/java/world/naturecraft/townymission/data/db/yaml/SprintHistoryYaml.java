/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db.yaml;

import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SprintEntry;
import world.naturecraft.townymission.components.containers.sql.SprintHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SprintHistoryYaml extends YamlStorage<SprintHistoryEntry> {

    protected SprintHistoryYaml(TownyMission instance, DbType dbType) {
        super(instance, dbType);
    }

    public void add(int season, int sprint, long startedTime, String rankJson) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".season", season);
        add(uuid + ".sprint", sprint);
        add(uuid + ".startedTime", startedTime);
        add(uuid + ".rankJson", rankJson);
    }

    public void update(String uuid, int season, int sprint, long startedTime, String rankJson) {
        set(uuid + ".season", season);
        set(uuid + ".sprint", sprint);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".rankJson", rankJson);
    }

    @Override
    protected List<SprintHistoryEntry> getEntries() {
        List<SprintHistoryEntry> entryList = new ArrayList<>();
        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new SprintHistoryEntry(
                    UUID.fromString(file.getString(key + ".uuid")),
                    file.getInt(key + ".season"),
                    file.getInt(key + ".sprint"),
                    file.getLong(key + ".startedTime"),
                    file.getString(key + ".rankJson")
            ));
        }

        return entryList;
    }
}
