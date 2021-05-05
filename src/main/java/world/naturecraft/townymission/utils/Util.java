package world.naturecraft.townymission.utils;

import world.naturecraft.townymission.enums.DbType;

import java.util.Locale;

public class Util {

    public static String getDbName(DbType dbType) {
        return "townymission_" + dbType.name().toLowerCase(Locale.ROOT);
    }
}
