/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.ConfigLoadingError;
import world.naturecraft.townymission.api.exceptions.ConfiguParsingException;
import world.naturecraft.townymission.components.containers.sql.MissionEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;

import javax.print.attribute.PrintJobAttributeSet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MissionYaml extends YamlStorage<MissionEntry> {

    public MissionYaml(TownyMission instance, DbType dbType) {
        super(instance, dbType);
    }

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

    public void update(String uuid, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID) {
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
        for (String key : file.getConfigurationSection("").getKeys(false)) {
            try {
                entryList.add(new MissionEntry(
                        UUID.fromString(file.getString(key + ".uuid")),
                        file.getString(key + ".missionType"),
                        file.getLong(key + ".addedTime"),
                        file.getLong(key + ".startedTime"),
                        file.getLong(key + ".allowedTime"),
                        file.getString(key + ".missionJson"),
                        file.getString(key + ".townName"),
                        file.getString(key + ".startedPlayerUUID")
                ));
            } catch (JsonProcessingException e) {
                throw new ConfiguParsingException(e);
            }
        }

        return entryList;
    }
}
