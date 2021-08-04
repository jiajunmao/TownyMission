package world.naturecraft.townymission.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.json.reward.RewardJson;
import world.naturecraft.townymission.config.RewardConfigParser;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.MultilineBuilder;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TownyMissionReward extends TownyMissionCommand implements TabExecutor, CommandExecutor {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionReward(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean sanityCheck(@NotNull Player player, @NotNull String[] args) {
        return new BukkitChecker(instance).target(player).silent(true)
                .hasPermission("townymission.player")
                .customCheck(() -> {
                    // /tms reward <sprint/season>
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("sprint") || args[1].equalsIgnoreCase("season")) {
                            return true;
                        }
                    }

                    onUnknown(player);
                    return false;
                }).check();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!sanityCheck(player, strings)) return false;

            RankType rankType = RankType.valueOf(strings[1].toUpperCase(Locale.ROOT));
            Map<Integer, List<RewardJson>> rewardMap = RewardConfigParser.getRankRewardsMap(rankType);

            MultilineBuilder multilineBuilder = new MultilineBuilder("&7------&eTowny Mission: Reward Rank&7------");
            for (int i : rewardMap.keySet()) {
                if (i != -1) {
                    multilineBuilder.add("&eRank " + i + ":");
                    for (RewardJson json : rewardMap.get(i)) {
                        multilineBuilder.add("&f- &6" + Util.capitalizeFirst(json.getRewardType().name()) + "&7: " + json.getDisplayLine());
                    }
                }
            }

            if (rewardMap.containsKey(-1)) {
                multilineBuilder.add("&eOthers:");
                for (RewardJson json : rewardMap.get(-1)) {
                    multilineBuilder.add("&f- &6" + Util.capitalizeFirst(json.getRewardType().name()) + "&7: " + json.getDisplayLine());
                }
            }

            String finalMsg = multilineBuilder.toString();
            ChatService.getInstance().sendMsg(player.getUniqueId(), finalMsg);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> tabList = new ArrayList<>();

        tabList.add("sprint");
        tabList.add("season");
        return tabList;
    }
}
