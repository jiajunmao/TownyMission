/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.sql;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.UUID;

public class CooldownEntry extends SqlEntry {

    private Town town;
    private long cooldown;

    public CooldownEntry(int id, Town town, long cooldown) {
        super(id, DbType.COOLDOWN);
        this.town = town;
        this.cooldown = cooldown;
    }

    public CooldownEntry(int id, String townUUID, long cooldown) throws NotRegisteredException {
        this(id, TownyAPI.getInstance().getDataSource().getTown(UUID.fromString(townUUID)), cooldown);
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }
}
