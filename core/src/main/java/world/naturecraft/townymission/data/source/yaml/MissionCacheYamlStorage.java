/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.source.yaml;

import world.naturecraft.townymission.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.storage.MissionCacheStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * The type Mission yaml.
 */
public class MissionCacheYamlStorage extends YamlStorage<MissionCacheEntry> implements MissionCacheStorage {

    /**
     * Instantiates a new Mission yaml.
     */
    public MissionCacheYamlStorage() {
        super(DbType.MISSION_CACHE);
    }

    @Override
    public void add(UUID playerUUID, MissionType missionType, int amount, long lastAttempted, int retryCount) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".playerUUID", playerUUID.toString());
        add(uuid + ".missionType", missionType.name());
        add(uuid + ".amount", amount);
        add(uuid + ".lastAttempted", lastAttempted);
        add(uuid + ".retryCount", retryCount);
    }

    @Override
    public void update(UUID uuid, UUID playerUUID, MissionType missionType, int amount, long lastAttempted, int retryCount) {
        add(uuid + ".playerUUID", playerUUID.toString());
        add(uuid + ".missionType", missionType.name());
        add(uuid + ".amount", amount);
        add(uuid + ".lastAttempted", lastAttempted);
        add(uuid + ".retryCount", retryCount);
    }

    @Override
    public List<MissionCacheEntry> getEntries() {
        List<MissionCacheEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            try {
                entryList.add(new MissionCacheEntry(
                        UUID.fromString(key),
                        UUID.fromString(file.getString(key + ".playerUUID")),
                        MissionType.valueOf(file.getString(key + ".missionType").toUpperCase(Locale.ROOT)),
                        file.getInt(key + ".amount"),
                        file.getLong(key + ".lastAttempted"),
                        file.getInt(key + ".retryCount")
                ));
            } catch (NullPointerException e) {
                remove(UUID.fromString(key));
            }
        }

        return entryList;
    }
}
