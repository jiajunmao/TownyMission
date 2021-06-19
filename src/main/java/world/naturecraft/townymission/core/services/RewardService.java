package world.naturecraft.townymission.core.services;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.api.exceptions.NotEnoughInvSlotException;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.bukkit.utils.RankUtil;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.bungee.utils.BungeeUtil;
import world.naturecraft.townymission.core.components.entity.ClaimEntry;
import world.naturecraft.townymission.core.components.entity.PluginMessage;
import world.naturecraft.townymission.core.components.entity.SeasonEntry;
import world.naturecraft.townymission.core.components.entity.SprintEntry;
import world.naturecraft.townymission.core.components.enums.RankType;
import world.naturecraft.townymission.core.components.enums.RewardMethod;
import world.naturecraft.townymission.core.components.enums.RewardType;
import world.naturecraft.townymission.core.components.json.reward.CommandRewardJson;
import world.naturecraft.townymission.core.components.json.reward.MoneyRewardJson;
import world.naturecraft.townymission.core.components.json.reward.ResourceRewardJson;
import world.naturecraft.townymission.core.components.json.reward.RewardJson;
import world.naturecraft.townymission.core.config.reward.RewardConfigParser;
import world.naturecraft.townymission.core.data.dao.ClaimDao;
import world.naturecraft.townymission.core.data.dao.SeasonDao;
import world.naturecraft.townymission.core.data.dao.SprintDao;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * The type Reward service.
 */
public class RewardService extends TownyMissionService {

    private static RewardService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static RewardService getInstance() {
        if (singleton == null) {
            singleton = new RewardService();
        }

        return singleton;
    }

    /**
     * Reward a player with the content in the RewarsJson
     * This assumes that the player is online
     *
     * @param claimEntry The entry containing the reward
     */
// TODO: Grab stuff from the freaking DAO, you are a service!
    public void claimEntry(UUID playerUUID, ClaimEntry claimEntry) {
        RewardJson rewardJson = claimEntry.getRewardJson();
        RewardType rewardType = rewardJson.getRewardType();
        String playerName;
        if (TownyMissionInstance.getInstance() instanceof TownyMissionBukkit) {
            playerName = BukkitUtil.getPlayerNameFromUUID(playerUUID);
        } else {
            playerName = BungeeUtil.getPlayerNameFromUUID(playerUUID);
        }

        switch (rewardType) {
            case COMMAND:
                CommandRewardJson commandRewardJson = (CommandRewardJson) rewardJson;
                String command = commandRewardJson.getCommand().replace("%player%", playerName);
                System.out.println("Dispatching command: " + command);
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                };

                TaskService.getInstance().runTaskAsync(r);
                ClaimDao.getInstance().remove(claimEntry);
                ChatService.getInstance().sendMsg(playerUUID, instance.getLangEntry("services.reward.onRewardCommand"));
                break;
            case POINTS:
                throw new IllegalStateException("Season point reward CANNOT be rewarded to individual player");
            case MONEY:
                MoneyRewardJson moneyRewardJson = (MoneyRewardJson) rewardJson;
                EconomyService.getInstance().depositBalance(playerUUID, moneyRewardJson.getAmount());
                ClaimDao.getInstance().remove(claimEntry);
                ChatService.getInstance().sendMsg(playerUUID, instance.getLangEntry("services.reward.onRewardMoney").replace("%amount%", String.valueOf(moneyRewardJson.getAmount())));
                break;
            case RESOURCE:
                ResourceRewardJson resourceRewardJson = (ResourceRewardJson) rewardJson;
                String material = resourceRewardJson.getType();
                int amount = resourceRewardJson.getAmount();
                int slotsRequired = amount / 64 + 1;

                if (PlayerService.getInstance().getNumEmptySlot(playerUUID) >= slotsRequired) {
                    while (amount > 64) {
                        PlayerService.getInstance().addItem(playerUUID, material, 64);
                        amount -= 64;
                    }

                    PlayerService.getInstance().addItem(playerUUID, material, amount);
                    ClaimDao.getInstance().remove(claimEntry);
                } else {
                    throw new NotEnoughInvSlotException();
                }

                ChatService.getInstance().sendMsg(playerUUID,
                        instance.getLangEntry("services.reward.onRewardResource")
                                .replace("%amount%", String.valueOf(resourceRewardJson.getAmount()))
                                .replace("%type%", resourceRewardJson.getType().toLowerCase(Locale.ROOT)));
                break;
        }
    }

    /**
     * Claim entry.
     *
     * @param playerUUID         the player
     * @param rewardJsonList the reward json list
     */
    public void claimEntry(UUID playerUUID, List<ClaimEntry> rewardJsonList) {
        for (ClaimEntry entry : rewardJsonList) {
            claimEntry(playerUUID, entry);
        }
    }

    /**
     * Reward town.
     *
     * @param rewardMethod the reward method
     * @param rewardJson   the reward json
     */
    public void rewardTown(UUID townUUID, RewardMethod rewardMethod, RewardJson rewardJson) {
        if (rewardJson.getRewardType().equals(RewardType.POINTS)) {
            // This is reward season point. Ignore RewardMethod.
            if (SeasonDao.getInstance().get(townUUID) == null) {
                SeasonDao.getInstance().add(
                        new SeasonEntry(
                                UUID.randomUUID(),
                                townUUID,
                                rewardJson.getAmount(),
                                instance.getStatsConfig().getInt("season.current")));
            } else {
                SeasonEntry seasonEntry = SeasonDao.getInstance().get(townUUID);
                seasonEntry.setSeasonPoint(seasonEntry.getSeasonPoint() + rewardJson.getAmount());
            }
        } else {
            // Giving individual reward. Count in RewardMethod
            switch (rewardMethod) {
                case INDIVIDUAL:
                    // This means rewarding everyone in the town everything on the list
                    List<Resident> residents = null;
                    for (Resident resident : residents) {
                        ClaimEntry entry = new ClaimEntry(
                                UUID.randomUUID(),
                                UUID.fromString(resident.getUUID().toString()),
                                rewardJson.getRewardType(),
                                rewardJson,
                                instance.getStatsConfig().getInt("season.current"),
                                instance.getStatsConfig().getInt("sprint.current"));
                        ClaimDao.getInstance().add(entry);
                    }
                    break;
                case EQUAL:
                    residents = null;
                    int numResidents = residents.size();
                    int share = rewardJson.getAmount() / numResidents + 1;
                    RewardJson copyRewardJseon = RewardJson.deepCopy(rewardJson);
                    copyRewardJseon.setAmount(share);
                    for (Resident resident : residents) {
                        ClaimEntry entry = new ClaimEntry(
                                UUID.randomUUID(),
                                UUID.fromString(resident.getUUID().toString()),
                                rewardJson.getRewardType(),
                                copyRewardJseon,
                                instance.getStatsConfig().getInt("season.current"),
                                instance.getStatsConfig().getInt("sprint.current"));
                        ClaimDao.getInstance().add(entry);
                    }
                    break;
                case CONTRIBUTIONS:
                    Map<String, Double> averageContribution = MissionService.getInstance().getAverageContributions(
                            instance.getStatsConfig().getInt("sprint.current"),
                            instance.getStatsConfig().getInt("season.current")
                    );

                    for (String playerUUID : averageContribution.keySet()) {
                        double percent = averageContribution.get(playerUUID);
                        RewardJson copyRewardJson = RewardJson.deepCopy(rewardJson);
                        copyRewardJson.setAmount((int) (copyRewardJson.getAmount() * percent + 1));
                        ClaimDao.getInstance().add(
                                new ClaimEntry(
                                        UUID.randomUUID(),
                                        UUID.fromString(playerUUID),
                                        rewardJson.getRewardType(),
                                        copyRewardJson,
                                        instance.getStatsConfig().getInt("season.current"),
                                        instance.getStatsConfig().getInt("sprint.current")));

                    }
            }
        }
    }

    /**
     * Reward all towns.
     *
     * @param rankType     the rank type
     * @param rewardMethod the reward method
     */
    public void rewardAllTowns(RankType rankType, RewardMethod rewardMethod) {

        switch (rankType) {
            case SPRINT:
                List<SprintEntry> sprintEntries = (List<SprintEntry>) RankUtil.sort(SprintDao.getInstance().getEntries());
                Map<Integer, List<RewardJson>> rewardsMap = RewardConfigParser.getRankRewardsMap(RankType.SPRINT);
                System.out.println("SprintEntries length: " + sprintEntries.size());
                System.out.println("RewardMap size: " + rewardsMap.keySet().size());
                for (Integer currentRank : rewardsMap.keySet()) {
                    List<RewardJson> rewardJsonList = rewardsMap.get(currentRank);

                    if (currentRank == -1) {
                        // This is rewarding other towns
                        int numRanked = rewardsMap.keySet().size();
                        for (int i = numRanked; i < sprintEntries.size(); i++) {
                            SprintEntry otherEntry = sprintEntries.get(i);
                            for (RewardJson rewardJson : rewardJsonList) {
                                rewardTown(otherEntry.getTownUUID(), rewardMethod, rewardJson);
                            }
                        }
                    } else if (currentRank != -1 && currentRank - 1 < sprintEntries.size()) {
                        // This is rewarding ranked towns
                        SprintEntry sprintEntry = sprintEntries.get(currentRank - 1);

                        for (RewardJson rewardJson : rewardJsonList) {
                            rewardTown(sprintEntry.getTownUUID(), rewardMethod, rewardJson);
                        }
                    }
                }
                break;
            case SEASON:
                List<SeasonEntry> seasonEntries = (List<SeasonEntry>) RankUtil.sort(SeasonDao.getInstance().getEntries());
                rewardsMap = RewardConfigParser.getRankRewardsMap(RankType.SEASON);

                for (Integer currentRank : rewardsMap.keySet()) {
                    List<RewardJson> rewardJsonList = rewardsMap.get(currentRank);

                    if (currentRank == -1) {
                        // This is rewarding other towns
                        int numRanked = rewardsMap.keySet().size();
                        for (int i = numRanked; i < seasonEntries.size(); i++) {
                            SeasonEntry otherEntry = seasonEntries.get(i);
                            for (RewardJson rewardJson : rewardJsonList) {
                                rewardTown(otherEntry.getTownUUID(), rewardMethod, rewardJson);
                            }
                        }
                    } else if (currentRank - 1 < seasonEntries.size()) {
                        SeasonEntry seasonEntry = seasonEntries.get(currentRank - 1);

                        for (RewardJson rewardJson : rewardJsonList) {
                            rewardTown(seasonEntry.getTownUUID(), rewardMethod, rewardJson);
                        }
                    }
                }
        }

    }
}
