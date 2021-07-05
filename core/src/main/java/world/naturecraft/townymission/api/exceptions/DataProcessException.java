/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.api.exceptions;

/**
 * The type Data process exception.
 */
public class DataProcessException extends RuntimeException {

    /**
     * Instantiates a new Data process exception.
     *
     * @param message the message
     */
    public DataProcessException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Data process exception.
     *
     * @param e the e
     */
    public DataProcessException(Exception e) {
        super(e);
    }

    /**
     * Instantiates a new Data process exception.
     */
    public DataProcessException() {
        this("Something went wrong during data processing.");
    }
}
