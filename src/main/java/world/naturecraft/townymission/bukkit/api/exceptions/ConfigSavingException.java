package world.naturecraft.townymission.bukkit.api.exceptions;

import java.rmi.server.ExportException;

public class ConfigSavingException extends RuntimeException {

    public ConfigSavingException(Exception e) {
        super(e);
    }
}
