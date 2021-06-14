/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.yaml;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Cooldown yaml.
 */
public class CooldownYamlStorage extends YamlStorage<CooldownEntry> {

    private static CooldownYamlStorage singleton;

    /**
     * Instantiates a new Cooldown yaml.
     *
     * @param instance the instance
     */
    public CooldownYamlStorage(TownyMission instance) {
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
            TownyMission townyMission = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            new CooldownYamlStorage(townyMission);
        }
        return singleton;
    }

    /**
     * Add.
     *
     * @param townUUID    the town uuid
     * @param startedTime the started time
     * @param cooldown    the cooldown
     */
    public void add(String townUUID, long startedTime, long cooldown) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".townUUID", townUUID);
        add(uuid + ".startedTime", startedTime);
        add(uuid + ".cooldown", cooldown);
    }

    /**
     * Update.
     *
     * @param uuid        the uuid
     * @param townUUID    the town uuid
     * @param startedTime the started time
     * @param cooldown    the cooldown
     */
    public void update(UUID uuid, String townUUID, long startedTime, long cooldown) {
        set(uuid + ".townUUID", townUUID);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".cooldown", cooldown);
    }

    @Override
    public List<CooldownEntry> getEntries() {
        List<CooldownEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            try {
                entryList.add(new CooldownEntry(
                        UUID.fromString(key),
                        file.getString(key + ".townUUID"),
                        file.getLong(key + ".startedTime"),
                        file.getLong(key + ".cooldown")
                ));
            } catch (NotRegisteredException notRegisteredException) {
                notRegisteredException.printStackTrace();
            }
        }

        return entryList;
    }
}