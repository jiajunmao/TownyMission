package world.naturecraft.townymission.utils;

import org.bukkit.Bukkit;
import world.naturecraft.naturelib.components.enums.StorageType;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.enums.DbType;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public static String mmDispalyName(String internalName) {
        StringBuilder sb = new StringBuilder();
        for (char c : internalName.toCharArray()) {
            if (c <= 'Z' && sb.length() != 0) {
                sb.append(" ");
            }

            sb.append(c);
        }

        return sb.toString();
    }

    public static List<Integer> versionParser(String version) {
        List<Integer> list = new ArrayList<>();
        while (version.contains(".")) {
            String tempstr = version.substring(0, version.indexOf("."));
            list.add(Integer.parseInt(tempstr));
            version = version.substring(version.indexOf(".") + 1);
        }
        list.add(Integer.parseInt(version));
        return list;
    }

    public static List<Integer> currBukkitVer() {
        return Util.versionParser(Bukkit.getVersion().substring(Bukkit.getVersion().indexOf("MC: ") + 4, Bukkit.getVersion().length() - 1));
    }

    public static long now() {
        return new Date().getTime();
    }
}
