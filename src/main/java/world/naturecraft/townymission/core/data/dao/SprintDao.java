/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.dao;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Server;
import world.naturecraft.townymission.core.components.entity.SprintEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.enums.ServerType;
import world.naturecraft.townymission.core.components.json.rank.TownRankJson;
import world.naturecraft.townymission.core.data.db.SprintStorage;
import world.naturecraft.townymission.core.services.StorageService;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Sprint dao.
 */
public class SprintDao extends Dao<SprintEntry> {

    private static SprintDao singleton;
    private final SprintStorage database;

    /**
     * Instantiates a new Sprint dao.
     */
    public SprintDao() {
        super(StorageService.getInstance().getStorage(DbType.SPRINT));
        this.database = StorageService.getInstance().getStorage(DbType.SPRINT);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintDao getInstance() {
        if (singleton == null) {
            singleton = new SprintDao();
        }
        return singleton;
    }

    public void update(SprintEntry entry) {
        if (get(entry.getTownID()) != null) {
            database.update(entry.getId(), entry.getTownID(), entry.getTownName(), entry.getNaturepoints(), entry.getSprint(), entry.getSeason());
        }
    }

    @Override
    public void remove(SprintEntry data) {
        database.remove(data.getId());
    }

    public void add(SprintEntry entry) {
        if (get(entry.getTownID()) == null) {
            database.add(entry.getTownID(), entry.getTownName(), entry.getNaturepoints(), entry.getSprint(), entry.getSeason());
        } else {
            update(entry);
        }
    }

    /**
     * Gets entries as json.
     *
     * @return the entries as json
     */
    public List<TownRankJson> getEntriesAsJson() {
        List<TownRankJson> rankJsons = new ArrayList<>();
        for (SprintEntry entry : getEntries()) {
            rankJsons.add(new TownRankJson(entry));
        }

        return rankJsons;
    }

    /**
     * Contains boolean.
     *
     * @param town the town
     * @return the boolean
     */
    public boolean contains(Town town) {
        for (SprintEntry entry : getEntries()) {
            if (entry.getTownID().equalsIgnoreCase(town.getUUID().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get sprint entry.
     *
     * @param townUUID the town uuid
     * @return the sprint entry
     */
    public SprintEntry get(String townUUID) {
        if (database.getEntries() == null)
            return null;

        for (SprintEntry s : database.getEntries()) {
            if (s.getTownID().equalsIgnoreCase(townUUID)) {
                return s;
            }
        }
        return null;
    }
}
