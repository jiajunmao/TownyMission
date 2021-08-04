/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import org.jetbrains.annotations.Nullable;
import world.naturecraft.naturelib.database.Dao;
import world.naturecraft.townymission.components.entity.SeasonEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.json.rank.TownRankJson;
import world.naturecraft.townymission.data.storage.SeasonStorage;
import world.naturecraft.townymission.services.StorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Season dao.
 */
public class SeasonDao extends Dao<SeasonEntry> {

    private static SeasonDao singleton;
    private final SeasonStorage database;

    /**
     * Instantiates a new Season dao.
     */
    public SeasonDao() {
        super(StorageService.getInstance().getStorage(DbType.SEASON));
        this.database = StorageService.getInstance().getStorage(DbType.SEASON);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonDao getInstance() {
        if (singleton == null) {
            singleton = new SeasonDao();
        }
        return singleton;
    }

    public void update(SeasonEntry entry) {
        if (get(entry.getTownUUID()) != null) {
            database.update(entry.getId(), entry.getTownUUID(), entry.getSeasonPoint(), entry.getSeason());
        }
    }

    @Override
    public void remove(SeasonEntry data) {
        database.remove(data.getId());
    }

    public void add(SeasonEntry entry) {
        database.add(entry.getTownUUID(), entry.getSeasonPoint(), entry.getSeason());
    }

    /**
     * Get season entry.
     *
     * @param townUUID the town uuid
     * @return the season entry
     */
    @Nullable
    public SeasonEntry get(UUID townUUID) {
        if (database.getEntries() == null)
            return null;

        for (SeasonEntry s : database.getEntries()) {
            if (s.getTownUUID().equals(townUUID)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Gets entries as json.
     *
     * @return the entries as json
     */
    public List<TownRankJson> getEntriesAsJson() {
        List<TownRankJson> rankJsons = new ArrayList<>();
        for (SeasonEntry entry : getEntries()) {
            rankJsons.add(new TownRankJson(entry));
        }

        return rankJsons;
    }
}
