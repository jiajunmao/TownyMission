/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.ConfigParsingException;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Mission yaml.
 */
public class MissionYamlStorage extends YamlStorage<MissionEntry> {

    private static MissionYamlStorage singleton;

    /**
     * Instantiates a new Mission yaml.
     *
     * @param instance the instance
     */
    public MissionYamlStorage(TownyMission instance) {
        super(instance, DbType.MISSION);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionYamlStorage getInstance() {
        if (singleton == null) {
            TownyMission townyMission = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            new MissionYamlStorage(townyMission);
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
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     */
    public void add(String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".missionType", missionType);
        add(uuid + ".addedTime", addedTime);
        add(uuid + ".startedTime", startedTime);
        add(uuid + ".allowedTime", allowedTime);
        add(uuid + ".missionJson", missionJson);
        add(uuid + ".townName", townName);
        add(uuid + ".startedPlayerUUID", startedPlayerUUID);
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
     * @param townName          the town name
     * @param startedPlayerUUID the started player uuid
     */
    public void update(UUID uuid, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID) {
        set(uuid + ".missionType", missionType);
        set(uuid + ".addedTime", addedTime);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".allowedTime", allowedTime);
        set(uuid + ".missionJson", missionJson);
        set(uuid + ".townName", townName);
        set(uuid + ".startedPlayerUUID", startedPlayerUUID);
    }

    @Override
    public List<MissionEntry> getEntries() {
        List<MissionEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            try {
                entryList.add(new MissionEntry(
                        UUID.fromString(key),
                        file.getString(key + ".missionType"),
                        file.getLong(key + ".addedTime"),
                        file.getLong(key + ".startedTime"),
                        file.getLong(key + ".allowedTime"),
                        file.getString(key + ".missionJson"),
                        file.getString(key + ".townName"),
                        file.getString(key + ".startedPlayerUUID")
                ));
            } catch (JsonProcessingException e) {
                throw new ConfigParsingException(e);
            }
        }

        return entryList;
    }
}
