/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.yaml;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.MissionCacheEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.db.MissionCacheStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * The type Mission yaml.
 */
public class MissionCacheYamlStorage extends YamlStorage<MissionCacheEntry> implements MissionCacheStorage {

    private static MissionCacheYamlStorage singleton;

    /**
     * Instantiates a new Mission yaml.
     *
     * @param instance the instance
     */
    public MissionCacheYamlStorage(TownyMissionInstance instance) {
        super(instance, DbType.MISSION_CACHE);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MissionCacheYamlStorage getInstance() {
        if (singleton == null) {
            new MissionCacheYamlStorage(TownyMissionInstance.getInstance());
        }
        return singleton;
    }

    @Override
    public void add(UUID playerUUID, MissionType missionType, int amount) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".playerUUID", playerUUID);
        add(uuid + ".missionType", missionType.name());
        add(uuid + ".amount", amount);
    }

    @Override
    public void update(UUID uuid, UUID playerUUID, MissionType missionType, int amount) {
        add(uuid + ".playerUUID", playerUUID);
        add(uuid + ".missionType", missionType.name());
        add(uuid + ".amount", amount);
    }

    @Override
    public List<MissionCacheEntry> getEntries() {
        List<MissionCacheEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            String startedPlayerUUID = file.getString(key + ".startedPlayerUUID");
            entryList.add(new MissionCacheEntry(
                    UUID.fromString(key),
                    UUID.fromString(file.getString(key + ".playerUUID")),
                    MissionType.valueOf(file.getString(key + ".missionType").toUpperCase(Locale.ROOT)),
                    file.getInt(key + ".amount")
            ));
        }

        return entryList;
    }
}
