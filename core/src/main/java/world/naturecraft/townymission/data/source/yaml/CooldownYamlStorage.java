/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.source.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.CooldownStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Cooldown yaml.
 */
public class CooldownYamlStorage extends YamlStorage<CooldownEntry> implements CooldownStorage {

    private static CooldownYamlStorage singleton;

    /**
     * Instantiates a new Cooldown yaml.
     *
     * @param instance the instance
     */
    public CooldownYamlStorage(TownyMissionInstance instance) {
        super(instance, DbType.COOLDOWN);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CooldownYamlStorage getInstance() {
        if (singleton == null) {
            new CooldownYamlStorage(TownyMissionInstance.getInstance());
        }
        return singleton;
    }

    /**
     * Add.
     *
     * @param townUUID the town uuid
     */
    public void add(UUID townUUID, String cooldownListJson) {
        String uuid = UUID.randomUUID().toString();

        super.add(uuid + ".townUUID", townUUID.toString());
        super.add(uuid + ".cooldownListJson", cooldownListJson);
    }

    /**
     * Update.
     *
     * @param uuid     the uuid
     * @param townUUID the town uuid
     */
    public void update(UUID uuid, UUID townUUID, String cooldownListJson) {
        set(uuid + ".townUUID", townUUID.toString());
        set(uuid + ".cooldownListJson", cooldownListJson);
    }

    @Override
    public List<CooldownEntry> getEntries() {
        List<CooldownEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new CooldownEntry(
                    UUID.fromString(key),
                    UUID.fromString(file.getString(key + ".townUUID")),
                    file.getString(key + ".cooldownListJson")
            ));
        }

        return entryList;
    }
}
