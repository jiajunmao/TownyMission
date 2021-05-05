/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

/**
 * The type Season rank.
 */
public class SeasonRank extends JsonEntry {

    private final String townId;
    private final String townName;
    private final int naturepoints;

    /**
     * Instantiates a new Season rank.
     *
     * @param townId       the town id
     * @param townName     the town name
     * @param naturepoints the naturepoints
     */
    public SeasonRank(String townId, String townName, int naturepoints) {
        this.townId = townId;
        this.townName = townName;
        this.naturepoints = naturepoints;
    }
}
