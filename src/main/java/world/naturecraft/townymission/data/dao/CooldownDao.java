/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.dao;

import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.api.exceptions.NotFoundException;
import world.naturecraft.townymission.components.containers.sql.CooldownEntry;
import world.naturecraft.townymission.data.db.sql.CooldownDatabase;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The type Cooldown dao.
 */
public class CooldownDao extends Dao<CooldownEntry> {

    private static CooldownDao singleton;
    private final CooldownDatabase db;

    /**
     * Instantiates a new Cooldown dao.
     */
    public CooldownDao() {
        this.db = CooldownDatabase.getInstance();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CooldownDao getInstance() {
        if (singleton == null) {
            singleton = new CooldownDao();
        }
        return singleton;
    }

    @Override
    public List<CooldownEntry> getEntries() {
        return db.getEntries();
    }

    @Override
    public void add(CooldownEntry data) {
        db.add(data.getTown().getUUID().toString(), data.getStartedTime(), data.getCooldown());
    }

    @Override
    public void update(CooldownEntry data) {
        db.update(data.getId(), data.getTown().getUUID().toString(), data.getStartedTime(), data.getCooldown());
    }

    @Override
    public void remove(CooldownEntry data) {
        db.remove(data.getId());
    }

    /**
     * Get cooldown entry.
     *
     * @param town the town
     * @return the cooldown entry
     */
    public CooldownEntry get(Town town) {
        for (CooldownEntry entry : getEntries()) {
            if (entry.getTown().equals(town)) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Is still in cooldown boolean.
     *
     * @param town the town
     * @return the boolean
     */
    public boolean isStillInCooldown(Town town) {
        Date date = new Date();
        if (get(town) == null)
            throw new NotFoundException();
        return get(town).getStartedTime() + get(town).getCooldown() >= date.getTime();
    }

    /**
     * Gets remaining.
     *
     * @param town the town
     * @return the remaining
     */
    public long getRemaining(Town town) {
        Date date = new Date();
        if (get(town) == null)
            throw new NotFoundException();
        return get(town).getStartedTime() + get(town).getCooldown() - date.getTime();
    }

    /**
     * Start cooldown.
     *
     * @param town     the town
     * @param cooldown the cooldown
     */
    public void startCooldown(Town town, long cooldown) {
        Date date = new Date();
        if (get(town) == null) {
            add(new CooldownEntry(UUID.randomUUID(), town, date.getTime(), cooldown));
        } else {
            CooldownEntry entry = get(town);
            entry.setStartedTime(date.getTime());
            entry.setCooldown(cooldown);
            update(entry);
        }
    }
}
