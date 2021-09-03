package world.naturecraft.townymission.services;

import com.palmergames.bukkit.towny.object.Town;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

public class PlaceholderBukkitService extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "townymission";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Barbadosian";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        switch (params) {
            case "town_rank_sprint":
                return town_rank_sprint(player);
            case "town_rank_season":
                return town_rank_season(player);
            case "town_rankPoint_sprint":
                return town_rankPoint_sprint(player);
            case "town_point_sprint":
                return town_point_sprint(player);
            case "town_missionOngoing":
                return town_missionOngoing(player);
            case "currSprint":
                return TownyMissionInstance.getInstance().getStatsConfig().getString("sprint.current");
            case "currSeason":
                return TownyMissionInstance.getInstance().getStatsConfig().getString("season.current");
        }

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
