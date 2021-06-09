/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.components.containers.sql.CooldownEntry;
import world.naturecraft.townymission.db.sql.CooldownDatabase;

import java.util.List;

public class CooldownDao extends Dao<CooldownEntry> {

    private CooldownDatabase db;

    public CooldownDao(CooldownDatabase db) {
        this.db = db;
    }

    @Override
    public List<CooldownEntry> getEntries() {
        return db.getEntries();
    }

    @Override
    public void add(CooldownEntry data) throws JsonProcessingException {
        db.add(data.getTown().getUUID().toString(), data.getCooldown());
    }

    @Override
    public void update(CooldownEntry data) throws JsonProcessingException {
        db.update(data.getId(), data.getTown().getUUID().toString(), data.getCooldown());
    }

    @Override
    public void remove(CooldownEntry data) {
        db.remove(data.getId());
    }
}
