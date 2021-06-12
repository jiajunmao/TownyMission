package world.naturecraft.townymission.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Util.
 */
public class Util {

    /**
     * Gets db name.
     *
     * @param dbType the db type
     * @return the db name
     */
    public static String getDbName(DbType dbType) {
        return "townymission_" + dbType.name().toLowerCase(Locale.ROOT);
    }

    /**
     * Send msg.
     *
     * @param sender  the sender
     * @param message the message
     */
    public static void sendMsg(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(translateColor(message));
        } else {
            sender.getServer().getLogger().info(translateColor(message));
        }
    }

    /**
     * Translate hex color string.
     *
     * @param startTag the start tag
     * @param endTag   the end tag
     * @param message  the message
     * @return the string
     */
    private static String translateHexColor(String startTag, String endTag, String message) {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        char COLOR_CHAR = ChatColor.COLOR_CHAR;
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

    /**
     * Translate vanilla color string.
     *
     * @param message the message
     * @return the string
     */
    private static String translateVanillaColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Translate color string.
     *
     * @param message the message
     * @return the string
     */
    public static String translateColor(String message) {
        return translateVanillaColor(translateHexColor("\\{#", "\\}", message));
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

    /**
     * Classify task entry map.
     *
     * @param list the list
     * @return the map
     */
    public static Map<MissionType, List<MissionEntry>> classifyTaskEntry(List<MissionEntry> list) {
        Map<MissionType, List<MissionEntry>> map = new HashMap<>();
        for (MissionType missionType : MissionType.values()) {
            map.put(missionType, new ArrayList<>());
        }

        for (MissionEntry e : list) {
            MissionType type = e.getMissionType();
            map.get(type).add(e);
        }

        return map;
    }

    /**
     * Gets ranking points.
     *
     * @param numResident the num resident
     * @param naturePoint the nature point
     * @param instance    the instance
     * @return the ranking points
     */
    public static int getRankingPoints(int numResident, int naturePoint, TownyMission instance) {
        int baseline = instance.getConfig().getInt("participants.sprintRewardBaseline");
        int memberScale = instance.getConfig().getInt("participants.sprintRewardMemberScale");
        int baselineCap = instance.getConfig().getInt("participants.sprintRewardBaselineCap");
        int increment = instance.getConfig().getInt("participants.sprintBaselineIncrement");
        int currentSprint = instance.getConfig().getInt("sprint.current");

        int realBaseline = Math.min(baseline + memberScale * (numResident - 1), baselineCap) + increment * (currentSprint - 1);
        return (naturePoint - realBaseline) / numResident;
    }

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
}
