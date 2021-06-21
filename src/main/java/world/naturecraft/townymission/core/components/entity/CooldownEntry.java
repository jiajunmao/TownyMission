/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.components.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.json.cooldown.CooldownListJson;

import java.util.Date;
import java.util.UUID;

/**
 * The type Cooldown entry.
 */
public class CooldownEntry extends DataEntity {

    // Map<StartedTime, Cooldown>
    private final CooldownListJson cooldownJsonList;
    private UUID townUUID;

    /**
     * Instantiates a new Cooldown entry.
     *
     * @param uuid     the id
     * @param townUUID the town
     */
    public CooldownEntry(UUID uuid, UUID townUUID) {
        super(uuid, DbType.COOLDOWN);
        this.townUUID = townUUID;
        cooldownJsonList = new CooldownListJson();
    }

    /**
     * Instantiates a new Cooldown entry.
     *
     * @param id               the id
     * @param townUUID         the town uuid
     * @param cooldownJsonList the cooldown json list
     * @throws JsonProcessingException the json processing exception
     */
    public CooldownEntry(UUID id, UUID townUUID, String cooldownJsonList) throws JsonProcessingException {
        super(id, DbType.COOLDOWN);
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