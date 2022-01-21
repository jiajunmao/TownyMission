package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Town;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.services.core.RankingService;
import world.naturecraft.townymission.services.core.TimerService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.List;
import java.util.UUID;

public class PlaceholderBukkitService extends PlaceholderExpansion {
    @Override
    public @NotNull
    String getIdentifier() {
        return "townymission";
    }

    @Override
    public @NotNull
    String getAuthor() {
        return "Barbadosian";
    }

    @Override
    public @NotNull
    String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("town_rank_sprint"))
            return town_rank_sprint(player);
        else if (params.equalsIgnoreCase("town_rank_season"))
            return town_rank_season(player);
        else if (params.equalsIgnoreCase("town_rankPoint_sprint"))
            return town_rankPoint_sprint(player);
        else if (params.equalsIgnoreCase("town_point_sprint"))
            return town_point_sprint(player);
        else if (params.equalsIgnoreCase("town_missionOngoing"))
            return town_missionOngoing(player);
        else if (params.startsWith("sprint_topTown_"))
            return sprint_topTown(params);
        else if (params.startsWith("season_topTown_"))
            return season_topTown(params);
        else if (params.equalsIgnoreCase("currSprint"))
            return TownyMissionInstance.getInstance().getStatsConfig().getString("sprint.current");
        else if (params.equalsIgnoreCase("currSeason"))
            return TownyMissionInstance.getInstance().getStatsConfig().getString("season.current");

        return null; // Placeholder is unknown by the Expansion
    }

    public String town_missionOngoing(OfflinePlayer player) {
        Town town = getTown(player);
        if (town == null) return "N/A";

        BukkitChecker checker = new BukkitChecker(TownyMissionInstance.getInstance()).target(player)
                .hasTown().hasStarted();

        if (checker.check()) {
            return "Yes";
        } else {
            return "No";
        }
    }

    public String town_point_sprint(OfflinePlayer player) {
        Town town = getTown(player);
        if (town == null) return "N/A";

        return String.valueOf(SprintDao.getInstance().get(town.getUUID()).getNaturepoints());
    }

    public String town_rankPoint_sprint(OfflinePlayer player) {
        Town town = getTown(player);

        if (town == null) return "N/A";

        return String.valueOf(RankingService.singleton.getRankingPoints(town.getUUID()));
    }

    public String town_rank_sprint(OfflinePlayer player) {
        Town town = getTown(player);

        if (town == null) return "N/A";

        Integer rank = RankingService.getInstance().getRank(town.getUUID(), RankType.SPRINT);

        if (rank == null) return "N/A";

        return String.valueOf(rank);
    }

    public String town_rank_season(OfflinePlayer player) {
        Town town = getTown(player);

        if (town == null) return "N/A";

        Integer rank = RankingService.getInstance().getRank(town.getUUID(), RankType.SEASON);

        if (rank == null) return "N/A";

        return String.valueOf(rank);
    }

    public String sprint_topTown(String placeholder) {
        List<Rankable> rankableList = RankingService.getInstance().getRanks(RankType.SPRINT);

        return topTown(placeholder, rankableList);
    }

    public String season_topTown(String placeholder) {
        List<Rankable> rankableList = RankingService.getInstance().getRanks(RankType.SEASON);

        return topTown(placeholder, rankableList);
    }

    private String topTown(String placeholder, List<Rankable> rankableList) {
        int index = 0;
        while (placeholder.indexOf("_", index) != -1) {
            index = placeholder.indexOf("_", index) + 1;
        }

        int topIdx = Integer.parseInt(placeholder.substring(index));
        if (topIdx > rankableList.size() || topIdx <= 0)
            return "N/A";

        TownyMissionInstance.getInstance().log("Ranking id: " + rankableList.get(topIdx - 1).getRankingId());
        return TownyUtil.getTown(UUID.fromString(rankableList.get(topIdx - 1).getRankingId())).getName();
    }

    private Town getTown(OfflinePlayer player) {
        BukkitChecker checker = new BukkitChecker(TownyMissionInstance.getInstance()).target(player)
                .hasTown()
                .customCheck(() -> {
                    if (TimerService.getInstance().isInInterval(RankType.SEASON) || TimerService.getInstance().isInInterval(RankType.SPRINT)) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), TownyMissionInstance.getInstance().getLangEntry("universal.onClickDuringRecess"));
                        return false;
                    }
                    return true;
                });

        if (!checker.check()) return null;

        return TownyUtil.residentOf(player);
    }

    @Override
    public String getRequiredPlugin() {
        return "TownyMission";
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin(getRequiredPlugin()) != null;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        return onRequest(player, identifier);
    }
}
