/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.containers.json;

public class SeasonRank extends JsonEntry {

    private final String townId;
    private final String townName;
    private final int naturepoints;

    public SeasonRank(String townId, String townName, int naturepoints) {
        this.townId = townId;
        this.townName = townName;
        this.naturepoints = naturepoints;
    }
}
