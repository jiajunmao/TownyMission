package world.naturecraft.townymission.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;

import java.util.UUID;

/**
 * The type Util.
 */
public class BukkitUtil {

    /**
     * Gets ranking points.
     *
     * @param numResident the num resident
     * @param naturePoint the nature point
     * @param instance    the instance
     * @return the ranking points
     */
    public static int getRankingPoints(int numResident, int naturePoint, TownyMissionBukkit instance) {
        int baseline = instance.getConfig().getInt("participants.sprintRewardBaseline");
        int memberScale = instance.getConfig().getInt("participants.sprintRewardMemberScale");
        int baselineCap = instance.getConfig().getInt("participants.sprintRewardBaselineCap");
        int increment = instance.getConfig().getInt("participants.sprintBaselineIncrement");
        int currentSprint = instance.getStatsConfig().getInt("sprint.current");

        int realBaseline = Math.min(baseline + memberScale * (numResident - 1), baselineCap) + increment * (currentSprint - 1);
        return (naturePoint - realBaseline) / numResident;
    }

    /**
     * Gets num empty slots in inventory.
     *
     * @param playerInventory the player inventory
     * @return the num empty slots in inventory
     */
    public static int getNumEmptySlotsInInventory(PlayerInventory playerInventory) {
        int num = 0;
        for (int i = 0; i < 36; i++) {
            if (playerInventory.getStorageContents()[i] == null
                    || playerInventory.getStorageContents()[i].getType().equals(Material.AIR)) {
                num++;
            }
        }

        return num;
    }

    /**
     * Gets player name from uuid.
     *
     * @param playerUUID the player uuid
     * @return the player name from uuid
     */
    public static String getPlayerNameFromUUID(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return player.getName();
    }
}
