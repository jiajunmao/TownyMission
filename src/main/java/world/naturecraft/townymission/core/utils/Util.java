package world.naturecraft.townymission.core.utils;

import world.naturecraft.townymission.core.components.enums.DbType;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * The type Util.
 */
public class Util {

    /**
     * Is int boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Is double boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Capitalize first string.
     *
     * @param str the str
     * @return the string
     */
    public static String capitalizeFirst(String str) {
        str = str.toLowerCase(Locale.ROOT);
        str = str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1);
        return str;
    }

    /**
     * Current time long.
     *
     * @return the long
     */
    public static long currentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Hr to ms long.
     *
     * @param hr the hr
     * @return the long
     */
    public static long hrToMs(int hr) {
        return (long) hr * 60 * 60 * 1000;
    }

    /**
     * Minute to ms long.
     *
     * @param minute the minute
     * @return the long
     */
    public static long minuteToMs(int minute) {
        return (long) minute * 60 * 1000;
    }

    /**
     * Ms to ticks long.
     *
     * @param milliseconds the milliseconds
     * @return the long
     */
    public static long msToTicks(long milliseconds) {
        return milliseconds / 1000 * 20;
    }

    public static String formatMilliseconds(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
    /**
     * Gets db name.
     *
     * @param dbType the db type
     * @return the db name
     */
    public static String getDbName(DbType dbType) {
        return "townymission_" + dbType.name().toLowerCase(Locale.ROOT);
    }
}
