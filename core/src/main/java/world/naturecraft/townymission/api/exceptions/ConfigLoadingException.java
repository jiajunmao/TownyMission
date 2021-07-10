/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.api.exceptions;

/**
 * The type Config loading error.
 */
public class ConfigLoadingException extends RuntimeException {

    /**
     * Instantiates a new Config loading error.
     *
     * @param e the e
     */
    public ConfigLoadingException(Exception e) {
        super(e);
    }
}
