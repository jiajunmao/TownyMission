/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.yaml;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SeasonHistoryEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Season history yaml.
 */
public class SeasonHistoryYaml extends YamlStorage<SeasonHistoryEntry> {

    private static SeasonHistoryYaml singleton;

    /**
     * Instantiates a new Season history yaml.
     *
     * @param instance the instance
     */
    public SeasonHistoryYaml(TownyMission instance) {
        super(instance, DbType.SEASON_HISTORY);
        singleton = this;
    }

    /**
     * Add.
     *
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void add(int season, long startedTime, String rankJson) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".season", season);
        add(uuid + ".startedTime", startedTime);
        add(uuid + ".rankJson", rankJson);
    }

    /**
     * Update.
     *
     * @param uuid        the uuid
     * @param season      the season
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public void update(UUID uuid, int season, long startedTime, String rankJson) {
        set(uuid + ".season", season);
        set(uuid + ".startedTime", startedTime);
        set(uuid + ".rankJson", rankJson);
    }

    @Override
    public List<SeasonHistoryEntry> getEntries() {
        List<SeasonHistoryEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new SeasonHistoryEntry(
                    UUID.fromString(key),
                    file.getInt(key + ".season"),
                    file.getLong(key + ".startedTime"),
                    file.getString(key + ".rankJson")
            ));
        }

        return entryList;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonHistoryYaml getInstance() {
        if (singleton == null) {
            TownyMission townyMission = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            new SeasonHistoryYaml(townyMission);
        }
        return singleton;
    }
}
