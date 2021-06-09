/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.api.exceptions;

import javax.xml.crypto.Data;

public class DataProcessException extends RuntimeException {

    public DataProcessException(String message) {
        super(message);
    }

    public DataProcessException(Exception e) {
        super(e);
    }

    public DataProcessException() {
        this("Something went wrong during data processing.");
    }
}
