/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.naturelib.components.DataEntity;
import world.naturecraft.naturelib.exceptions.ConfigParsingException;
import world.naturecraft.townymission.components.json.cooldown.CooldownJson;
import world.naturecraft.townymission.components.json.cooldown.CooldownListJson;

import java.util.Date;
import java.util.UUID;

/**
 * The type Cooldown entry.
 */
public class CooldownEntry extends DataEntity {

    // Map<StartedTime, Cooldown>
    private UUID townUUID;
    private final CooldownListJson cooldownJsonList;

    /**
     * Instantiates a new Cooldown entry.
     *
     * @param uuid     the id
     * @param townUUID the town
     */
    public CooldownEntry(UUID uuid, UUID townUUID) {
        super(uuid);
        this.townUUID = townUUID;
        cooldownJsonList = new CooldownListJson();
    }

    /**
     * Instantiates a new Cooldown entry.
     *
     * @param id               the id
     * @param townUUID         the town uuid
     * @param cooldownJsonList the cooldown json list
     */
    public CooldownEntry(UUID id, UUID townUUID, String cooldownJsonList) {
        super(id);
        this.townUUID = townUUID;
        this.cooldownJsonList = CooldownListJson.parse(cooldownJsonList);
    }

    /**
     * Gets town uuid.
     *
     * @return the town uuid
     */
    public UUID getTownUUID() {
        return townUUID;
    }

    /**
     * Sets town uuid.
     *
     * @param townUUID the town uuid
     */
    public void setTownUUID(UUID townUUID) {
        this.townUUID = townUUID;
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
     * Start cooldown.
     *
     * @param cooldown the cooldown
     */
    public void startCooldown(int numMission, long cooldown) {
        long timeNow = new Date().getTime();
        CooldownJson newCooldown = new CooldownJson(timeNow, cooldown);
        cooldownJsonList.addCooldown(numMission, newCooldown);
    }
}