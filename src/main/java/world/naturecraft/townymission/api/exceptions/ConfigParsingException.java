/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.api.exceptions;

/**
 * The type Configu parsing exception.
 */
public class ConfigParsingException extends RuntimeException {

    /**
     * Instantiates a new Configu parsing exception.
     *
     * @param e the e
     */
    public ConfigParsingException(Exception e) {
        super(e);
    }

    public ConfigParsingException(String message) {
        super (message);
    }

    public ConfigParsingException(String message, Throwable e) {
        super(message, e);
    }

}