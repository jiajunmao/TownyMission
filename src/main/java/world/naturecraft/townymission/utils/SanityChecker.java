package world.naturecraft.townymission.utils;

import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.dao.TaskDao;
import world.naturecraft.townymission.db.sql.TaskDatabase;

import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The type Sanity checker.
 */
public class SanityChecker {

    private TownyMission instance;

    private boolean checkHasTown;

    private boolean checkIsMayor;

    private boolean checkHasStarted;

    private boolean checkIsMissionType;
    private MissionType missionType;

    private final List<String> permissions;
    private final List<BooleanChecker> customChecks;

    private final TaskDao taskDao;
    private Player player;

    /**
     * Instantiates a new Sanity checker.
     *
     * @param instance the instance
     */
    public SanityChecker(TownyMission instance) {
        this.instance = instance;
        taskDao = new TaskDao((TaskDatabase) instance.getDb(DbType.TASK));
        customChecks = new ArrayList<>();
        permissions = new ArrayList<>();
    }

    /**
     * Target sanity checker.
     *
     * @param player the player
     * @return the sanity checker
     */
    public SanityChecker target(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Has town sanity checker.
     *
     * @return the sanity checker
     */
    public SanityChecker hasTown() {
        checkHasTown = true;
        return this;
    }

    /**
     * Is mayor sanity checker.
     *
     * @return the sanity checker
     */
    public SanityChecker isMayor() {
        checkIsMayor = true;
        return this;
    }

    /**
     * Has started sanity checker.
     *
     * @return the sanity checker
     */
    public SanityChecker hasStarted() {
        checkHasStarted = true;
        return this;
    }

    /**
     * Is mission type sanity checker.
     *
     * @param type the type
     * @return the sanity checker
     */
    public SanityChecker isMissionType(MissionType type) {
        checkIsMissionType = true;
        missionType = type;
        return this;
    }

    public SanityChecker hasPermission(String permission) {
        permissions.add(permission);
        return this;
    }

    /**
     * Custom check sanity checker.
     *
     * @param checker the checker
     * @return the sanity checker
     */
    public SanityChecker customCheck(BooleanChecker checker) {
        // Other logic

        customChecks.add(checker);
        return this;
    }

    /**
     * Check boolean.
     *
     * @return the boolean
     */
    public boolean check() {

        if (checkHasTown) {
            if (TownyUtil.residentOf(player) == null) {
                Util.sendMsg(player, Util.getLangEntry("commands.sanityChecker.onNoTown", instance));
                return false;
            }
        }

        if (checkIsMayor) {
            if (!checkHasTown)
                return false;
            if (TownyUtil.mayorOf(player) == null) {
                Util.sendMsg(player, Util.getLangEntry("commands.sanityChecker.onNotMayor", instance));
                return false;
            }
        }

        if (checkHasStarted) {
            if (!checkHasTown)
                return false;
            if (taskDao.getStartedMission(TownyUtil.residentOf(player)) == null) {
                Util.sendMsg(player, Util.getLangEntry("commands.sanityChecker.onNoStartedMission", instance));
                return false;
            }
        }

        if (checkIsMissionType) {
            if (!checkHasStarted)
                return false;
            if (!taskDao.getStartedMission(TownyUtil.residentOf(player)).getMissionType().equals(missionType)) {
                Util.sendMsg(player, Util.getLangEntry("commands.sanityChecker.onMissionTypeMismatch", instance).replace("%missionType%", missionType.name().toLowerCase(Locale.ROOT)));
                return false;
            }
        }

        if (permissions.size() != 0) {
            for (String s : permissions) {
                if (!player.hasPermission(s))
                    Util.sendMsg(player, Util.getLangEntry("commands.sanityChecker.onNoPermission", instance).replace("%permission%", s));
                    return false;
            }
            return true;
        }

        if (customChecks.size() != 0) {
            for (BooleanChecker checker : customChecks) {
                if (!checker.check())
                    return false;
            }
        }

        return true;
    }

    /**
     * The interface Boolean checker.
     */
    public interface BooleanChecker {
        /**
         * Check boolean.
         *
         * @return the boolean
         */
        boolean check();
    }
}