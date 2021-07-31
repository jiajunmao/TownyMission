package world.naturecraft.townymission.services;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.data.dao.SeasonDao;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.utils.RankUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class RankingService {

    public static RankingService singleton;

    public static RankingService getInstance() {
        if (singleton == null) {
            singleton = new RankingService();
        }

        return singleton;
    }

    // This returns the sprint ranking point
    public int getRankingPoints(UUID townUUID) {
        TownyMissionInstance instance = TownyMissionInstance.getInstance();

        int currentSprint = instance.getStatsConfig().getInt("sprint.current");
        int baseline = instance.getInstanceConfig().getInt("participants.sprintRewardBaseline");
        int memberScale = instance.getInstanceConfig().getInt("participants.sprintRewardMemberScale");
        int baselineCap = instance.getInstanceConfig().getInt("participants.sprintRewardBaselineCap");
        int baselineIncrement = instance.getInstanceConfig().getInt("participants.sprintBaselineIncrement");


        int realBaseline = baseline + (TownyService.getInstance().getNumResidents(townUUID) - 1) * memberScale + (currentSprint - 1) * baselineIncrement;
        realBaseline = realBaseline > baselineCap ? baseline : realBaseline;

        int naturepoints = SprintDao.getInstance().get(townUUID).getNaturepoints();
        int rankingPoints = (naturepoints - realBaseline) / TownyService.getInstance().getNumResidents(townUUID);
        rankingPoints = Math.max(rankingPoints, 0);
        return rankingPoints;
    }

    public <T extends Rankable> List<T> getRanks(RankType rankType) {
        List<T> rankables = null;
        switch (rankType) {
            case SPRINT:
                rankables = new ArrayList<T>((Collection<? extends T>) RankUtil.sort(SprintDao.getInstance().getEntries()));
                break;
            case SEASON:
                rankables = new ArrayList<T>((Collection<? extends T>) RankUtil.sort(SeasonDao.getInstance().getEntries()));
                break;
        }

        List<T> finalList = new ArrayList<>();
        for (T r : rankables) {
            if (r.getRankingFactor() != 0) {
                finalList.add(r);
            }
        }

        return finalList;
    }

    public Integer getRank(UUID townUUID, RankType rankType) {
        switch (rankType) {
            case SPRINT:
                return getRank(townUUID, true);
            case SEASON:
                return getRank(townUUID, false);
            default:
                return null;
        }
    }

    private Integer getRank(UUID townUUID, boolean isSprint) {
        List<Rankable> entryList;
        if (isSprint) {
            entryList = (List<Rankable>) RankUtil.sort(SprintDao.getInstance().getEntries());
        } else {
            entryList = (List<Rankable>) RankUtil.sort(SeasonDao.getInstance().getEntries());
        }

        int index = 0;
        boolean found = false;
        for (Rankable rankable : entryList) {
            if (rankable.getRankingId().equalsIgnoreCase(townUUID.toString())) {
                found = true;
                break;
            }
            index++;
        }

        if (found) {
            return (index + 1);
        } else {
            return null;
        }
    }
}
