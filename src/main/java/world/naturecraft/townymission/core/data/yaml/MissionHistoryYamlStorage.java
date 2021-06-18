/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.api.exceptions.ConfigParsingException;
import world.naturecraft.townymission.core.components.entity.MissionHistoryEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.data.db.MissionHistoryStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Mission history yaml.
 */
public class MissionHistoryYamlStorage extends YamlStorage<MissionHistoryEntry> implements MissionHistoryStorage {

    private static MissionHistoryYamlStorage singleton;

    /**
     * Instantiates a new Mission history yaml.
     *
     * @param instance the instance
     */
    public MissionHistoryYamlStorage(TownyMissionInstance instance) {
        super(instance, DbType.MISSION_HISTORY);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionHistoryYamlStorage getInstance() {
        if (singleton == null) {
            new MissionHistoryYamlStorage(TownyMissionInstance.getInstance());
        }
        return singleton;
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
        add(uuid + ".townUUID", townUUID);
        add(uuid + ".startedPlayerUUID", startedPlayerUUID);
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
            try {
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
            } catch (JsonProcessingException e) {
                throw new ConfigParsingException(e);
            }
        }

        return entryList;
    }
}
