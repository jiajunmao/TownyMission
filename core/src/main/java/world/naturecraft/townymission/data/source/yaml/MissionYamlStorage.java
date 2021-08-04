/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.source.yaml;

import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.naturelib.database.YamlStorage;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.MissionStorage;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Mission yaml.
 */
public class MissionYamlStorage extends YamlStorage<MissionEntry> implements MissionStorage {

    /**
     * Instantiates a new Mission yaml.
     */
    public MissionYamlStorage() {
        super(Util.getDbName(DbType.MISSION, StorageType.YAML));
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
     */
    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".missionType", missionType);
        add(uuid + ".addedTime", addedTime);
        add(uuid + ".startedTime", startedTime);
        add(uuid + ".allowedTime", allowedTime);
        add(uuid + ".missionJson", missionJson);
        add(uuid + ".townUUID", townUUID.toString());
        if (startedPlayerUUID != null) {
            add(uuid + ".startedPlayerUUID", startedPlayerUUID.toString());
        }
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
     */
    public void update(UUID uuid, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID) {
        set(uuid + ".missionType", missionType);
        set(uuid + ".addedTime", addedTime);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".allowedTime", allowedTime);
        set(uuid + ".missionJson", missionJson);
        set(uuid + ".townUUID", townUUID.toString());
        set(uuid + ".startedPlayerUUID", startedPlayerUUID.toString());
    }

    @Override
    public List<MissionEntry> getEntries() {
        List<MissionEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            String startedPlayerUUID = file.getString(key + ".startedPlayerUUID");
            entryList.add(new MissionEntry(
                    UUID.fromString(key),
                    file.getString(key + ".missionType"),
                    file.getLong(key + ".addedTime"),
                    file.getLong(key + ".startedTime"),
                    file.getLong(key + ".allowedTime"),
                    file.getString(key + ".missionJson"),
                    UUID.fromString(file.getString(key + ".townUUID")),
                    startedPlayerUUID == null || startedPlayerUUID.equals("null") ? null : UUID.fromString(file.getString(key + ".startedPlayerUUID"))
            ));
        }

        return entryList;
    }
}
