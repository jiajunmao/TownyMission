/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.ConfiguParsingException;
import world.naturecraft.townymission.components.containers.sql.MissionEntry;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeasonYaml extends YamlStorage<SeasonEntry> {

    protected SeasonYaml(TownyMission instance, DbType dbType) {
        super(instance, dbType);
    }

    public void add(String townUUID, String townName, int seasonPoint, int season) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".townUUID", townUUID);
        add(uuid + ".townName", townName);
        add(uuid + ".seasonPoint", seasonPoint);
        add(uuid + ".season", season);
    }

    public void update(String uuid, String townUUID, String townName, int seasonPoint, int season) {
        set(uuid + ".townUUID", townUUID);
        set(uuid + ".townName", townName);
        set(uuid + ".seasonPoint", seasonPoint);
        set(uuid + ".season", season);
    }

    @Override
    protected List<SeasonEntry> getEntries() {
        List<SeasonEntry> entryList = new ArrayList<>();
        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new SeasonEntry(
                    UUID.fromString(file.getString(key + ".uuid")),
                    file.getString(key + ".townUUID"),
                    file.getString(key + ".townName"),
                    file.getInt(key + ".seasonPoint"),
                    file.getInt(key + ".season")
            ));
        }

        return entryList;
    }
}
