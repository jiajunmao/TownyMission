/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.entity;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.UUID;

/**
 * The type Cooldown entry.
 */
public class CooldownEntry extends DataEntity {

    private Town town;
    private long startedTime;
    private long cooldown;

    /**
     * Instantiates a new Cooldown entry.
     *
     * @param id          the id
     * @param town        the town
     * @param startedTime the started time
     * @param cooldown    the cooldown
     */
    public CooldownEntry(UUID id, Town town, long startedTime, long cooldown) {
        super(id, DbType.COOLDOWN);
        this.town = town;
        this.startedTime = startedTime;
        this.cooldown = cooldown;
    }

    /**
     * Instantiates a new Cooldown entry.
     *
     * @param id          the id
     * @param townUUID    the town uuid
     * @param startedTime the started time
     * @param cooldown    the cooldown
     * @throws NotRegisteredException the not registered exception
     */
    public CooldownEntry(UUID id, String townUUID, long startedTime, long cooldown) throws NotRegisteredException {
        this(id, TownyAPI.getInstance().getDataSource().getTown(UUID.fromString(townUUID)), startedTime, cooldown);
    }

    /**
     * Gets town.
     *
     * @return the town
     */
    public Town getTown() {
        return town;
    }

    /**
     * Sets town.
     *
     * @param town the town
     */
    public void setTown(Town town) {
        this.town = town;
    }

    /**
     * Gets cooldown.
     *
     * @return the cooldown
     */
    public long getCooldown() {
        return cooldown;
    }

    /**
     * Sets cooldown.
     *
     * @param cooldown the cooldown
     */
    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    /**
     * Gets started time.
     *
     * @return the started time
     */
    public long getStartedTime() {
        return startedTime;
    }

    /**
     * Sets started time.
     *
     * @param startedTime the started time
     */
    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }
}
