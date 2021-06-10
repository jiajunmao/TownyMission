/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.api.exceptions;

import world.naturecraft.townymission.components.containers.sql.MissionEntry;

public class NotStartedException extends Throwable {

    private MissionEntry entry;

    public NotStartedException(MissionEntry entry) {
        this.entry = entry;
    }
}
