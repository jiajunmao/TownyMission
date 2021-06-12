/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.yaml;

import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Season yaml.
 */
public class SeasonYaml extends YamlStorage<SeasonEntry> {

    private static SeasonYaml singleton;

    /**
     * Instantiates a new Season yaml.
     *
     * @param instance the instance
     */
    protected SeasonYaml(TownyMission instance) {
        super(instance, DbType.SEASON_HISTORY);
        singleton = this;
    }

    /**
     * Add.
     *
     * @param townUUID    the town uuid
     * @param townName    the town name
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void add(String townUUID, String townName, int seasonPoint, int season) {
        String uuid = UUID.randomUUID().toString();

        add(uuid + ".townUUID", townUUID);
        add(uuid + ".townName", townName);
        add(uuid + ".seasonPoint", seasonPoint);
        add(uuid + ".season", season);
    }

    /**
     * Update.
     *
     * @param uuid        the uuid
     * @param townUUID    the town uuid
     * @param townName    the town name
     * @param seasonPoint the season point
     * @param season      the season
     */
    public void update(UUID uuid, String townUUID, String townName, int seasonPoint, int season) {
        set(uuid + ".townUUID", townUUID);
        set(uuid + ".townName", townName);
        set(uuid + ".seasonPoint", seasonPoint);
        set(uuid + ".season", season);
    }

    @Override
    public List<SeasonEntry> getEntries() {
        List<SeasonEntry> entryList = new ArrayList<>();

        if (file.getConfigurationSection("") == null)
            return entryList;

        for (String key : file.getConfigurationSection("").getKeys(false)) {
            entryList.add(new SeasonEntry(
                    UUID.fromString(key),
                    file.getString(key + ".townUUID"),
                    file.getString(key + ".townName"),
                    file.getInt(key + ".seasonPoint"),
                    file.getInt(key + ".season")
            ));
        }

        return entryList;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonYaml getInstance() {
        if (singleton == null) {
            TownyMission townyMission = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            new SeasonYaml(townyMission);
        }
        return singleton;
    }
}
