/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.api.exceptions.NotFoundException;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.data.dao.CooldownDao;

import java.util.Date;
import java.util.UUID;

/**
 * The type Cooldown service.
 */
public class CooldownService extends TownyMissionService {

    private static CooldownService singleton;
    private TownyMission instance;

    public CooldownService(TownyMission instance) {
        this.instance = instance;
    }

    public static CooldownService getInstance() {
        if (singleton == null) {
            TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            singleton = new CooldownService(instance);
        }

        return singleton;
    }
    /**
     * Is still in cooldown boolean.
     *
     * @param town the town
     * @return the boolean
     */
    public boolean isStillInCooldown(Town town) {
        Date date = new Date();
        if (CooldownDao.getInstance().get(town) == null)
            throw new NotFoundException();
        return CooldownDao.getInstance().get(town).getStartedTime() + CooldownDao.getInstance().get(town).getCooldown() >= date.getTime();
    }

    /**
     * Gets remaining.
     *
     * @param town the town
     * @return the remaining
     */
    public long getRemaining(Town town) {
        Date date = new Date();
        if (CooldownDao.getInstance().get(town) == null)
            throw new NotFoundException();
        return CooldownDao.getInstance().get(town).getStartedTime() + CooldownDao.getInstance().get(town).getCooldown() - date.getTime();
    }

    /**
     * Start cooldown.
     *
     * @param town     the town
     * @param cooldown the cooldown
     */
    public void startCooldown(Town town, long cooldown) {
        Date date = new Date();
        // This means that the town does not exist in the db yet
        if (CooldownDao.getInstance().get(town) == null) {
            CooldownDao.getInstance().add(new CooldownEntry(UUID.randomUUID(), town, date.getTime(), cooldown));
        } else {
            CooldownEntry entry = CooldownDao.getInstance().get(town);
            entry.setStartedTime(date.getTime());
            entry.setCooldown(cooldown);
            CooldownDao.getInstance().update(entry);
        }
    }
}
