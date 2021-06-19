/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.services;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.core.components.entity.CooldownEntry;
import world.naturecraft.townymission.core.data.dao.CooldownDao;

import java.util.Date;
import java.util.UUID;

/**
 * The type Cooldown service.
 */
public class CooldownService extends TownyMissionService {

    private static CooldownService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CooldownService getInstance() {
        if (singleton == null) {
            singleton = new CooldownService();
        }

        return singleton;
    }

    /**
     * Start cooldown.
     *
     * @param townUUID the town uuid
     * @param cooldown the cooldown
     */
    public void startCooldown(UUID townUUID, long cooldown) {
        Date date = new Date();
        // This means that the town does not exist in the db yet
        if (CooldownDao.getInstance().get(townUUID) == null) {
            CooldownDao.getInstance().add(new CooldownEntry(UUID.randomUUID(), townUUID));
        }

        CooldownEntry entry = CooldownDao.getInstance().get(townUUID);
        entry.startCooldown(cooldown);
        CooldownDao.getInstance().update(entry);
    }

    /**
     * Gets num addable.
     *
     * @param townUUID the town uuid
     * @return the num addable
     */
    public int getNumAddable(UUID townUUID) {
        int amount = instance.getInstanceConfig().getInt("mission.amount");
        CooldownEntry entry = CooldownDao.getInstance().get(townUUID);

        if (entry == null) return amount;
        int finished = entry.getNumFinished(true);
        CooldownDao.getInstance().update(entry);
        return finished;
    }
}
