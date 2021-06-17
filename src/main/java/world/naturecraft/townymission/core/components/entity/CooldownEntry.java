/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.components.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.json.cooldown.CooldownListJson;

import java.util.Date;
import java.util.UUID;

/**
 * The type Cooldown entry.
 */
public class CooldownEntry extends DataEntity {

    private Town town;
    // Map<StartedTime, Cooldown>
    private final CooldownListJson cooldownJsonList;

    /**
     * Instantiates a new Cooldown entry.
     *
     * @param id   the id
     * @param town the town
     */
    public CooldownEntry(UUID id, Town town) {
        super(id, DbType.COOLDOWN);
        this.town = town;
        cooldownJsonList = new CooldownListJson();
    }

    /**
     * Instantiates a new Cooldown entry.
     *
     * @param id       the id
     * @param townUUID the town uuid
     * @throws NotRegisteredException the not registered exception
     */
    public CooldownEntry(UUID id, String townUUID) throws NotRegisteredException {
        this(id, TownyAPI.getInstance().getDataSource().getTown(UUID.fromString(townUUID)));
    }

    /**
     * Instantiates a new Cooldown entry.
     *
     * @param id               the id
     * @param townUUID         the town uuid
     * @param cooldownJsonList the cooldown json list
     * @throws NotRegisteredException  the not registered exception
     * @throws JsonProcessingException the json processing exception
     */
    public CooldownEntry(UUID id, String townUUID, String cooldownJsonList) throws NotRegisteredException, JsonProcessingException {
        super(id, DbType.COOLDOWN);
        this.town = TownyAPI.getInstance().getDataSource().getTown(UUID.fromString(townUUID));
        System.out.println("Parsing: " + cooldownJsonList);
        this.cooldownJsonList = CooldownListJson.parse(cooldownJsonList);
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
     * Gets cooldown json list.
     *
     * @return the cooldown json list
     */
    public CooldownListJson getCooldownJsonList() {
        return cooldownJsonList;
    }

    /**
     * Gets num finished.
     *
     * @param remove the remove
     * @return the num finished
     */
    public int getNumFinished(boolean remove) {
        return cooldownJsonList.getNumFinished(remove);
    }

    /**
     * Gets num total.
     *
     * @return the num total
     */
    public int getNumTotal() {
        return cooldownJsonList.getNumTotal();
    }

    /**
     * Start cooldown.
     *
     * @param cooldown the cooldown
     */
    public void startCooldown(long cooldown) {
        long timeNow = new Date().getTime();
        cooldownJsonList.addCooldown(timeNow, cooldown);
    }

    /**
     * Gets cooldowns as string.
     *
     * @return the cooldowns as string
     */
    public String getCooldownsAsString() {
        try {
            return cooldownJsonList.toJson();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}