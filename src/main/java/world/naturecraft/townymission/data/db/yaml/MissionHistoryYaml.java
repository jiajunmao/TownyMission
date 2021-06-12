/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.ConfiguParsingException;
import world.naturecraft.townymission.components.containers.sql.MissionEntry;
import world.naturecraft.townymission.components.containers.sql.MissionHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MissionHistoryYaml extends YamlStorage<MissionHistoryEntry> {

    protected MissionHistoryYaml(TownyMission instance, DbType dbType) {
        super(instance, dbType);
    }

    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".missionType", missionType);
        add(uuid + ".addedTime", addedTime);
        add(uuid + ".startedTime", startedTime);
        add(uuid + ".allowedTime", allowedTime);
        add(uuid + ".missionJson", missionJson);
        add(uuid + ".townName", townName);
        add(uuid + ".startedPlayerUUID", startedPlayerUUID);
        add(uuid + ".completedTime", completedTime);
        add(uuid + ".isClaimed", isClaimed);
        add(uuid + ".sprint", sprint);
        add(uuid + ".season", season);
    }

    public void update(String uuid, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season) {
        set(uuid + ".missionType", missionType);
        set(uuid + ".addedTime", addedTime);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".allowedTime", allowedTime);
        set(uuid + ".missionJson", missionJson);
        set(uuid + ".townName", townName);
        set(uuid + ".startedPlayerUUID", startedPlayerUUID);
        set(uuid + ".completedTime", completedTime);
        set(uuid + ".isClaimed", isClaimed);
        set(uuid + ".sprint", sprint);
        set(uuid + ".season", season);
    }

    @Override
    protected List getEntries() {
        List<MissionHistoryEntry> entryList = new ArrayList<>();
        for (String key : file.getConfigurationSection("").getKeys(false)) {
            try {
                entryList.add(new MissionHistoryEntry(
                        UUID.fromString(file.getString(key + ".uuid")),
                        file.getString(key + ".missionType"),
                        file.getLong(key + ".addedTime"),
                        file.getLong(key + ".startedTime"),
                        file.getLong(key + ".allowedTime"),
                        file.getString(key + ".missionJson"),
                        file.getString(key + ".townName"),
                        file.getString(key + ".startedPlayerUUID"),
                        file.getLong(key + ".completedTime"),
                        file.getBoolean(key + ".isClaimed"),
                        file.getInt(key + ".sprint"),
                        file.getInt(key + ".season")
                ));
            } catch (JsonProcessingException e) {
                throw new ConfiguParsingException(e);
            }
        }

        return entryList;
    }
}
