/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.bukkit.api.exceptions;

/**
 * The type Config loading error.
 */
public class ConfigLoadingError extends RuntimeException {

    /**
     * Instantiates a new Config loading error.
     *
     * @param e the e
     */
    public ConfigLoadingError(Exception e) {
        super(e);
    }
}
