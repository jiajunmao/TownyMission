package world.naturecraft.townymission.utils;

import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.enums.DbType;

import java.io.File;
import java.util.Locale;

public class Util extends world.naturecraft.naturelib.utils.Util {

    public static String getDbName(DbType dbType, StorageType storageType) {
        switch (storageType) {
            case YAML:
                String fileName = dbType.toString().toLowerCase(Locale.ROOT) + ".yml";
                String filePath = "datastore" + File.separator + fileName;
                return filePath;
            case MYSQL:
                String name = TownyMissionInstance.getInstance().getInstanceConfig().getString("database.prefix") + dbType.name().toLowerCase(Locale.ROOT);
                return name;
        }

        return null;
    }
}
