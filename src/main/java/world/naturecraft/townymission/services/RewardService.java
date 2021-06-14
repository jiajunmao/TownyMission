package world.naturecraft.townymission.services;

import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Economy.CMIEconomyAcount;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.entity.ClaimEntry;
import world.naturecraft.townymission.components.entity.SeasonEntry;
import world.naturecraft.townymission.components.entity.SprintEntry;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.enums.RewardMethod;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.components.json.reward.CommandRewardJson;
import world.naturecraft.townymission.components.json.reward.MoneyRewardJson;
import world.naturecraft.townymission.components.json.reward.ResourceRewardJson;
import world.naturecraft.townymission.components.json.reward.RewardJson;
import world.naturecraft.townymission.config.reward.RewardConfigParser;
import world.naturecraft.townymission.data.dao.ClaimDao;
import world.naturecraft.townymission.data.dao.MissionHistoryDao;
import world.naturecraft.townymission.data.dao.SeasonDao;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.utils.RankUtil;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type Reward service.
 */
public class RewardService extends TownyMissionService {

    private static RewardService singleton;
    private final TownyMission instance;

    /**
     * Instantiates a new Reward service.
     *
     * @param instance the instance
     */
    public RewardService(TownyMission instance) {
        this.instance = instance;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static RewardService getInstance() {
        if (singleton == null) {
            TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            singleton = new RewardService(instance);
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
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                ClaimDao.getInstance().remove(claimEntry);
                break;
            case POINTS:
                throw new IllegalStateException("Season point reward CANNOT be rewarded to individual player");
            case MONEY:
                MoneyRewardJson moneyRewardJson = (MoneyRewardJson) rewardJson;
                CMIUser cmiUser = new CMIUser(player.getUniqueId());
                CMIEconomyAcount economyAcount = new CMIEconomyAcount(cmiUser);
                economyAcount.deposit(moneyRewardJson.getAmount());
                ClaimDao.getInstance().remove(claimEntry);
                break;
            case RESOURCE:
                ResourceRewardJson resourceRewardJson = (ResourceRewardJson) rewardJson;
                Material material = resourceRewardJson.getType();
                int amount = resourceRewardJson.getAmount();
                int slotsRequired = amount / 64 + 1;

                if (Util.getNumEmptySlotsInInventory(player.getInventory()) >= slotsRequired) {
                    while (amount > 64) {
                        ItemStack itemStack = new ItemStack(material, 64);
                        player.getInventory().addItem(itemStack);
                        amount -= 64;
                    }

                    ItemStack itemStack = new ItemStack(material, amount);
                    player.getInventory().addItem(itemStack);
                    ClaimDao.getInstance().remove(claimEntry);
                } else {
                    // Not enough slot
                }
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
        if (rewardJson.getRewardType().equals(RewardType.POINTS)) {
            // This is reward season point. Ignore RewardMethod.
            if (SeasonDao.getInstance().get(town.getUUID().toString()) == null) {
                SeasonDao.getInstance().add(
                        new SeasonEntry(
                                UUID.randomUUID(),
                                town.getUUID().toString(),
                                town.getName(),
                                rewardJson.getAmount(),
                                instance.getConfig().getInt("season.current")));
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
                                UUID.fromString(resident.getPlayer().getUniqueId().toString()),
                                rewardJson,
                                instance.getConfig().getInt("season.current"),
                                instance.getConfig().getInt("sprint.current"));
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
                                UUID.fromString(resident.getPlayer().getUniqueId().toString()),
                                copyRewardJseon,
                                instance.getConfig().getInt("season.current"),
                                instance.getConfig().getInt("sprint.current"));
                        ClaimDao.getInstance().add(entry);
                    }
                    break;
                case CONTRIBUTIONS:
                    Map<String, Double> averageContribution = MissionService.getInstance().getAverageContributions(
                            instance.getConfig().getInt("sprint.current"),
                            instance.getConfig().getInt("season.current")
                    );

                    for (String playerUUID : averageContribution.keySet()) {
                        double percent = averageContribution.get(playerUUID);
                        RewardJson copyRewardJson = RewardJson.deepCopy(rewardJson);
                        copyRewardJson.setAmount((int) (copyRewardJson.getAmount() * percent + 1));
                        ClaimDao.getInstance().add(
                                new ClaimEntry(
                                        UUID.randomUUID(),
                                        UUID.fromString(playerUUID),
                                        copyRewardJson,
                                        instance.getConfig().getInt("season.current"),
                                        instance.getConfig().getInt("sprint.current")));

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
        //TODO: Separate RankTypes
        List<SprintEntry> sprintEntries = (List<SprintEntry>) RankUtil.sort(SprintDao.getInstance().getEntries());
        Map<Integer, List<RewardJson>> rewardsMap = RewardConfigParser.getRankRewardsMap(RankType.SPRINT);

        for (Integer currentRank : rewardsMap.keySet()) {
            List<RewardJson> rewardJsonList = rewardsMap.get(currentRank);
            if (currentRank - 1 < sprintEntries.size()) {
                SprintEntry sprintEntry = sprintEntries.get(currentRank - 1);
                Town town = TownyUtil.getTownByName(sprintEntry.getTownName());

                for (RewardJson rewardJson : rewardJsonList) {
                    rewardTown(town, rewardMethod, rewardJson);
                }
            }
        }
    }
}
