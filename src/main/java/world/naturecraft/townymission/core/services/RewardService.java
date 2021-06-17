package world.naturecraft.townymission.core.services;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.bukkit.api.exceptions.NotEnoughInvSlotException;
import world.naturecraft.townymission.core.components.entity.ClaimEntry;
import world.naturecraft.townymission.core.components.entity.SeasonEntry;
import world.naturecraft.townymission.core.components.entity.SprintEntry;
import world.naturecraft.townymission.core.components.enums.RankType;
import world.naturecraft.townymission.core.components.enums.RewardMethod;
import world.naturecraft.townymission.core.components.enums.RewardType;
import world.naturecraft.townymission.core.components.enums.ServerType;
import world.naturecraft.townymission.core.components.json.reward.CommandRewardJson;
import world.naturecraft.townymission.core.components.json.reward.MoneyRewardJson;
import world.naturecraft.townymission.core.components.json.reward.ResourceRewardJson;
import world.naturecraft.townymission.core.components.json.reward.RewardJson;
import world.naturecraft.townymission.core.config.reward.RewardConfigParser;
import world.naturecraft.townymission.core.data.dao.ClaimDao;
import world.naturecraft.townymission.core.data.dao.SeasonDao;
import world.naturecraft.townymission.core.data.dao.SprintDao;
import world.naturecraft.townymission.bukkit.utils.RankUtil;
import world.naturecraft.townymission.bukkit.utils.TownyUtil;
import world.naturecraft.townymission.bukkit.utils.BukkitUtil;

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
     * Instantiates a new Reward service.
     *
     * @param instance the instance
     */
    public RewardService(TownyMissionInstance instance) {
        super(instance);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static RewardService getInstance() {
        if (singleton == null) {
            singleton = new RewardService(TownyMissionInstance.getInstance());
        }

        return singleton;
    }

    /**
     * Reward a player with the content in the RewarsJson
     * This assumes that the player is online
     *
     * @param player     The online player
     * @param claimEntry The entry containing the reward
     */
    // TODO: Grab stuff from the freaking DAO, you are a service!
    public void claimEntry(Player player, ClaimEntry claimEntry) {
        RewardJson rewardJson = claimEntry.getRewardJson();
        RewardType rewardType = rewardJson.getRewardType();
        switch (rewardType) {
            case COMMAND:
                CommandRewardJson commandRewardJson = (CommandRewardJson) rewardJson;
                String command = commandRewardJson.getCommand().replace("%player%", player.getName());
                System.out.println("Dispatching command: " +  command);
                BukkitRunnable r = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                };

                TaskService.runTaskAsync(r);
                ClaimDao.getInstance().remove(claimEntry);
                BukkitUtil.sendMsg(player, instance.getLangEntry("services.reward.onRewardCommand"));
                break;
            case POINTS:
                throw new IllegalStateException("Season point reward CANNOT be rewarded to individual player");
            case MONEY:
                MoneyRewardJson moneyRewardJson = (MoneyRewardJson) rewardJson;
                EconomyService.getInstance().depositBalance(player, moneyRewardJson.getAmount());
                ClaimDao.getInstance().remove(claimEntry);
                BukkitUtil.sendMsg(player, instance.getLangEntry("services.reward.onRewardMoney").replace("%amount%", String.valueOf(moneyRewardJson.getAmount())));
                break;
            case RESOURCE:
                ResourceRewardJson resourceRewardJson = (ResourceRewardJson) rewardJson;
                Material material = resourceRewardJson.getType();
                int amount = resourceRewardJson.getAmount();
                int slotsRequired = amount / 64 + 1;

                if (BukkitUtil.getNumEmptySlotsInInventory(player.getInventory()) >= slotsRequired) {
                    while (amount > 64) {
                        ItemStack itemStack = new ItemStack(material, 64);
                        player.getInventory().addItem(itemStack);
                        amount -= 64;
                    }

                    ItemStack itemStack = new ItemStack(material, amount);
                    player.getInventory().addItem(itemStack);
                    ClaimDao.getInstance().remove(claimEntry);
                } else {
                    throw new NotEnoughInvSlotException();
                }

                BukkitUtil.sendMsg(player,
                        instance.getLangEntry("services.reward.onRewardResource")
                                .replace("%amount%", String.valueOf(resourceRewardJson.getAmount()))
                                .replace("%type%", resourceRewardJson.getType().name().toLowerCase(Locale.ROOT)));
                break;
        }
    }

    /**
     * Claim entry.
     *
     * @param player         the player
     * @param rewardJsonList the reward json list
     */
    public void claimEntry(Player player, List<ClaimEntry> rewardJsonList) {
        for (ClaimEntry entry : rewardJsonList) {
            claimEntry(player, entry);
        }
    }

    /**
     * Reward town.
     *
     * @param town         the town
     * @param rewardMethod the reward method
     * @param rewardJson   the reward json
     */
    public void rewardTown(Town town, RewardMethod rewardMethod, RewardJson rewardJson) {
        System.out.println("Rewarding town " + town.getName() + " with " + rewardJson.getDisplayLine() + " using " + rewardMethod);
        if (rewardJson.getRewardType().equals(RewardType.POINTS)) {
            // This is reward season point. Ignore RewardMethod.
            if (SeasonDao.getInstance().get(town.getUUID().toString()) == null) {
                SeasonDao.getInstance().add(
                        new SeasonEntry(
                                UUID.randomUUID(),
                                town.getUUID().toString(),
                                town.getName(),
                                rewardJson.getAmount(),
                                instance.getStatsConfig().getInt("season.current")));
            } else {
                SeasonEntry seasonEntry = SeasonDao.getInstance().get(town.getUUID().toString());
                seasonEntry.setSeasonPoint(seasonEntry.getSeasonPoint() + rewardJson.getAmount());
            }
        } else {
            // Giving individual reward. Count in RewardMethod
            switch (rewardMethod) {
                case INDIVIDUAL:
                    // This means rewarding everyone in the town everything on the list
                    List<Resident> residents = town.getResidents();
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
                    residents = town.getResidents();
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
                            Town town = TownyUtil.getTownByName(otherEntry.getTownName());
                            for (RewardJson rewardJson : rewardJsonList) {
                                rewardTown(town, rewardMethod, rewardJson);
                            }
                        }
                    } else if (currentRank != -1 && currentRank - 1 < sprintEntries.size()) {
                        // This is rewarding ranked towns
                        SprintEntry sprintEntry = sprintEntries.get(currentRank - 1);
                        Town town = TownyUtil.getTownByName(sprintEntry.getTownName());

                        for (RewardJson rewardJson : rewardJsonList) {
                            rewardTown(town, rewardMethod, rewardJson);
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
                            Town town = TownyUtil.getTownByName(otherEntry.getTownName());
                            for (RewardJson rewardJson : rewardJsonList) {
                                rewardTown(town, rewardMethod, rewardJson);
                            }
                        }
                    } else if (currentRank - 1 < seasonEntries.size()) {
                        SeasonEntry seasonEntry = seasonEntries.get(currentRank - 1);
                        Town town = TownyUtil.getTownByName(seasonEntry.getTownName());

                        for (RewardJson rewardJson : rewardJsonList) {
                            rewardTown(town, rewardMethod, rewardJson);
                        }
                    }
                }
        }

    }
}
