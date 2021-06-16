/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.services;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.entity.CooldownEntry;
import world.naturecraft.townymission.core.data.dao.CooldownDao;

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
     * Start cooldown.
     *
     * @param town     the town
     * @param cooldown the cooldown
     */
    public void startCooldown(Town town, long cooldown) {
        Date date = new Date();
        // This means that the town does not exist in the db yet
        if (CooldownDao.getInstance().get(town) == null) {
            CooldownDao.getInstance().add(new CooldownEntry(UUID.randomUUID(), town));
        }

        CooldownEntry entry = CooldownDao.getInstance().get(town);
        entry.startCooldown(cooldown);
        CooldownDao.getInstance().update(entry);
    }

    public int getNumAddable(Town town) {
        int amount = instance.getConfig().getInt("mission.amount");
        CooldownEntry entry = CooldownDao.getInstance().get(town);

        if (entry == null) return amount;
        int finished = entry.getNumFinished(true);
        CooldownDao.getInstance().update(entry);
        return finished;
    }
}
