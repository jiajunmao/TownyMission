package world.naturecraft.townymission.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import world.naturecraft.naturelib.NaturePlugin;
import world.naturecraft.naturelib.utils.BooleanChecker;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.ChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The type Sanity checker.
 */
public class BukkitChecker {

    private final List<String> permissions;
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
        permissions.add(permission);
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
            if (missionDao.getStartedMission(TownyUtil.residentOf(player).getUUID()) == null) {
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

            MissionEntry entry = MissionDao.getInstance().getStartedMission(TownyUtil.residentOf(player).getUUID());
            if (entry.isTimedout()) {
                if (!isSilent)
                    ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.sanityChecker.onMissionTimedOut"));
                return false;
            }
        }

        if (checkIsMissionType) {
            if (!checkHasStarted)
                return false;
            if (!missionDao.getStartedMission(TownyUtil.residentOf(player).getUUID()).getMissionType().equals(missionType)) {
                if (!isSilent)
                    ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.sanityChecker.onMissionTypeMismatch").replace("%missionType%", missionType.name().toLowerCase(Locale.ROOT)));
                return false;
            }
        }

        if (permissions.size() != 0) {
            for (String s : permissions) {
                if (Bukkit.getPlayer(player.getUniqueId()).isOnline()) {
                    Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
                    if (!onlinePlayer.hasPermission(s)) {
                        if (!isSilent)
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.sanityChecker.onNoPermission").replace("%permission%", s));
                        return false;
                    }
                } else {
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