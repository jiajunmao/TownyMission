/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services.core;

import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.json.cooldown.CooldownJson;
import world.naturecraft.townymission.data.dao.CooldownDao;
import world.naturecraft.townymission.services.TownyMissionService;

import java.util.*;

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
    public void startCooldown(UUID townUUID, int numMission, long cooldown) {
        Date date = new Date();
        // This means that the town does not exist in the db yet
        if (CooldownDao.getInstance().get(townUUID) == null) {
            CooldownDao.getInstance().add(new CooldownEntry(UUID.randomUUID(), townUUID));
        }

        CooldownEntry entry = CooldownDao.getInstance().get(townUUID);
        entry.startCooldown(numMission, cooldown);
        CooldownDao.getInstance().update(entry);
    }

    public List<Integer> getInCooldown(UUID townUUID) {
        CooldownEntry entry = CooldownDao.getInstance().get(townUUID);
        if (entry == null)
            return new ArrayList<>();

        Map<Integer, CooldownJson> cooldowenMap = entry.getCooldownJsonList().getCooldownMap();
        List<Integer> cooldownList = new ArrayList<>(cooldowenMap.keySet());
        for (Integer i : cooldowenMap.keySet()) {
            if (cooldowenMap.get(i).isFinished()) {
                cooldownList.remove(i);
            }
        }
        return cooldownList;
    }
}
