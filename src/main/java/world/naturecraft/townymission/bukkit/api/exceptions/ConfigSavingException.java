package world.naturecraft.townymission.bukkit.api.exceptions;

import java.rmi.server.ExportException;

/**
 * The type Config saving exception.
 */
public class ConfigSavingException extends RuntimeException {

    /**
     * Instantiates a new Config saving exception.
     *
     * @param e the e
     */
    public ConfigSavingException(Exception e) {
        super(e);
    }
}
