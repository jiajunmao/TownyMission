/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db.yaml;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.CooldownEntry;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Cooldown yaml.
 */
public class CooldownYaml extends YamlStorage<CooldownEntry> {

    /**
     * Instantiates a new Cooldown yaml.
     *
     * @param instance the instance
     * @param dbType   the db type
     */
    protected CooldownYaml(TownyMission instance, DbType dbType) {
        super(instance, dbType);
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
    public void update(String uuid, String townUUID, long startedTime, long cooldown) {
        set(uuid + ".townUUID", townUUID);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".cooldown", cooldown);
    }

    @Override
    protected List<CooldownEntry> getEntries() {
        List<CooldownEntry> entryList = new ArrayList<>();
        for (String key : file.getConfigurationSection("").getKeys(false)) {
            try {
                entryList.add(new CooldownEntry(
                        UUID.fromString(file.getString(key + ".uuid")),
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
