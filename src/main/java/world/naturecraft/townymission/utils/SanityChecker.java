package world.naturecraft.townymission.utils;

import org.bukkit.entity.Player;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.dao.TaskDao;
import world.naturecraft.townymission.db.sql.TaskDatabase;

import java.util.ArrayList;
import java.util.List;

public class SanityChecker {

    private boolean checkHasTown;

    private boolean checkIsMayor;

    private boolean checkHasStarted;

    private boolean checkIsMissionType;
    private MissionType missionType;

    private final List<BooleanChecker> customChecks;

    private final TaskDao taskDao;
    private Player player;

    public SanityChecker(TownyMission instance) {
        taskDao = new TaskDao((TaskDatabase) instance.getDb(DbType.TASK));
        customChecks = new ArrayList<>();
    }

    public SanityChecker target(Player player) {
        this.player = player;
        return this;
    }

    public SanityChecker hasTown() {
        checkHasTown = true;
        return this;
    }

    public SanityChecker isMayor() {
        checkIsMayor = true;
        return this;
    }

    public SanityChecker hasStarted() {
        checkHasStarted = true;
        return this;
    }

    public SanityChecker isMissionType(MissionType type) {
        checkIsMissionType = true;
        missionType = type;
        return this;
    }

    public SanityChecker customCheck(BooleanChecker checker) {
        // Other logic

        customChecks.add(checker);
        return this;
    }

    public boolean check() {

        if (checkHasTown) {
            if (TownyUtil.residentOf(player) == null)
                return false;
        }

        if (checkIsMayor) {
            if (!checkHasTown)
                return false;
            if (TownyUtil.mayorOf(player) == null) {
                return false;
            }
        }

        if (checkHasStarted) {
            if (!checkHasTown)
                return false;
            if (taskDao.getStartedMission(TownyUtil.residentOf(player)) == null)
                return false;
        }

        if (checkIsMissionType) {
            if (!checkHasStarted)
                return false;
            if (!taskDao.getStartedMission(TownyUtil.residentOf(player)).getTaskType().equalsIgnoreCase(missionType.name()))
                return false;
        }

        if (customChecks.size() != 0) {
            for (BooleanChecker checker : customChecks) {
                if (!checker.check())
                    return false;
            }
        }

        return true;
    }

    public interface BooleanChecker {
        boolean check();
    }
}