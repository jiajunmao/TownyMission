package world.naturecraft.townymission.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import world.naturecraft.naturelib.utils.BooleanChecker;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.MissionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The type Sanity checker.
 */
public class BukkitChecker {

    private final List<List<String>> permissions;
    private final List<BooleanChecker> customChecks;
    private final MissionDao missionDao;
    private final TownyMissionBukkit instance;
    private boolean checkHasTown;
    private boolean checkIsMayor;
    private boolean checkHasStarted;
    private boolean checkIsTimedOut;
    private boolean checkIsMissionType;
    private MissionType missionType;
    private OfflinePlayer player;
    private boolean isSilent;

    /**
     * Instantiates a new Sanity checker.
     *
     * @param instance the instance
     */
    public BukkitChecker(TownyMissionBukkit instance) {
        this.instance = instance;
        missionDao = MissionDao.getInstance();
        customChecks = new ArrayList<>();
        permissions = new ArrayList<>();
        isSilent = false;
    }

    /**
     * Target sanity checker.
     *
     * @param player the player
     * @return the sanity checker
     */
    public BukkitChecker target(OfflinePlayer player) {
        this.player = player;
        return this;
    }

    /**
     * Silent sanity checker.
     *
     * @param isSilent the is silent
     * @return the sanity checker
     */
    public BukkitChecker silent(boolean isSilent) {
        this.isSilent = isSilent;
        return this;
    }

    /**
     * Has town sanity checker.
     *
     * @return the sanity checker
     */
    public BukkitChecker hasTown() {
        checkHasTown = true;
        return this;
    }

    /**
     * Is mayor sanity checker.
     *
     * @return the sanity checker
     */
    public BukkitChecker isMayor() {
        checkIsMayor = true;
        return this;
    }

    /**
     * Has started sanity checker.
     *
     * @return the sanity checker
     */
    public BukkitChecker hasStarted() {
        checkHasStarted = true;
        return this;
    }

    public BukkitChecker isTimedOut() {
        checkIsTimedOut = true;
        return this;
    }

    /**
     * Is mission type sanity checker.
     *
     * @param type the type
     * @return the sanity checker
     */
    public BukkitChecker isMissionType(MissionType type) {
        checkIsMissionType = true;
        missionType = type;
        return this;
    }

    /**
     * Has permission sanity checker.
     *
     * @param permission the permission
     * @return the sanity checker
     */
    public BukkitChecker hasPermission(String permission) {
        List<String> permissions = new ArrayList<>();
        permissions.add(permission);
        this.permissions.add(permissions);
        return this;
    }

    public BukkitChecker hasPermission(String[] permissions) {
        List<String> tempPerm = new ArrayList<>();
        for (String s : permissions) {
            tempPerm.add(s);
        }
        this.permissions.add(tempPerm);
        return this;
    }

    /**
     * Custom check sanity checker.
     *
     * @param checker the checker
     * @return the sanity checker
     */
    public BukkitChecker customCheck(BooleanChecker checker) {
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
                if (!isSilent)
                    ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.sanityChecker.onNoTown"));
                return false;
            }
        }

        if (checkIsMayor) {
            if (!checkHasTown)
                return false;
            if (TownyUtil.mayorOf(player) == null) {
                if (!isSilent)
                    ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.sanityChecker.onNotMayor"));
                return false;
            }
        }

        if (checkHasStarted) {
            if (!checkHasTown)
                return false;
            if (!MissionService.getInstance().hasStarted(TownyUtil.residentOf(player).getUUID())) {
                if (!isSilent)
                    ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.sanityChecker.onNoStartedMission"));
                return false;
            }
        }

        if (checkIsTimedOut) {
            if (!checkHasTown)
                return false;
            if (!checkHasStarted)
                return false;

            MissionEntry entry = MissionDao.getInstance().getStartedMissions(TownyUtil.residentOf(player).getUUID()).get(0);
            if (entry.isTimedout()) {
                if (!isSilent)
                    ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.sanityChecker.onMissionTimedOut"));
                return false;
            }
        }

        if (checkIsMissionType) {
            if (!checkHasStarted)
                return false;
            if (!missionDao.getStartedMissions(TownyUtil.residentOf(player).getUUID()).get(0).getMissionType().equals(missionType)) {
                if (!isSilent)
                    ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.sanityChecker.onMissionTypeMismatch").replace("%missionType%", missionType.name().toLowerCase(Locale.ROOT)));
                return false;
            }
        }

        if (permissions.size() != 0) {
            for (List<String> strList : permissions) {
                // Perm in this list should be ORing
                if (!Bukkit.getPlayer(player.getUniqueId()).isOnline()) return false;

                Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
                boolean hasPerm = false;
                StringBuilder permStr = new StringBuilder();
                for (int i = 0; i < strList.size(); i++) {
                    String s = strList.get(i);
                    permStr.append(s);
                    if (i != strList.size() - 1)
                        permStr.append(" OR ");
                    hasPerm = hasPerm || onlinePlayer.hasPermission(s);
                }

                if (!hasPerm) {
                    if (!isSilent) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.sanityChecker.onNoPermission").replace("%permission%", permStr));
                    }
                    return false;
                }
            }
        }

        if (customChecks.size() != 0) {
            for (BooleanChecker checker : customChecks) {
                if (!checker.check()) {
                    return false;
                }
            }

        }
        return true;
    }
}