/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.source.yaml;

import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.naturelib.database.YamlStorage;
import world.naturecraft.townymission.components.entity.MissionHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.MissionHistoryStorage;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Mission history yaml.
 */
public class MissionHistoryYamlStorage extends YamlStorage<MissionHistoryEntry> implements MissionHistoryStorage {

    /**
     * Instantiates a new Mission history yaml.
     */
    public MissionHistoryYamlStorage() {
        super(Util.getDbName(DbType.MISSION_HISTORY, StorageType.YAML));
    }

    /**
     * Add.
     *
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the mission json
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player uuid
     * @param completedTime     the completed time
     * @param isClaimed         the is claimed
     * @param sprint            the sprint
     * @param season            the season
     */
    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".missionType", missionType);
        add(uuid + ".addedTime", addedTime);
        add(uuid + ".startedTime", startedTime);
        add(uuid + ".allowedTime", allowedTime);
        add(uuid + ".missionJson", missionJson);
        add(uuid + ".townUUID", townUUID.toString());
        add(uuid + ".startedPlayerUUID", startedPlayerUUID.toString());
        add(uuid + ".completedTime", completedTime);
        add(uuid + ".isClaimed", isClaimed);
        add(uuid + ".sprint", sprint);
        add(uuid + ".season", season);
    }

    /**
     * Update.
     *
     * @param uuid              the uuid
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the mission json
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player uuid
     * @param completedTime     the completed time
     * @param isClaimed         the is claimed
     * @param sprint            the sprint
     * @param season            the season
     */
    public void update(UUID uuid, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID, long completedTime, boolean isClaimed, int sprint, int season) {
        set(uuid + ".missionType", missionType);
        set(uuid + ".addedTime", addedTime);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".allowedTime", allowedTime);
        set(uuid + ".missionJson", missionJson);
        set(uuid + ".townUUID", townUUID);
        set(uuid + ".startedPlayerUUID", startedPlayerUUID);
        set(uuid + ".completedTime", completedTime);
        set(uuid + ".isClaimed", isClaimed);
        set(uuid + ".sprint", sprint);
        set(uuid + ".season", season);
    }

    @Override
    public List getEntries() {
        List<MissionHistoryEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new MissionHistoryEntry(
                    UUID.fromString(key),
                    file.getString(key + ".missionType"),
                    file.getLong(key + ".addedTime"),
                    file.getLong(key + ".startedTime"),
                    file.getLong(key + ".allowedTime"),
                    file.getString(key + ".missionJson"),
                    UUID.fromString(file.getString(key + ".townUUID")),
                    UUID.fromString(file.getString(key + ".startedPlayerUUID")),
                    file.getLong(key + ".completedTime"),
                    file.getBoolean(key + ".isClaimed"),
                    file.getInt(key + ".sprint"),
                    file.getInt(key + ".season")
            ));
        }

        return entryList;
    }
}
