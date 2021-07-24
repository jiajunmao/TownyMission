package world.naturecraft.townymission.services;

import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.data.dao.SeasonDao;
import world.naturecraft.townymission.data.dao.SprintDao;
import world.naturecraft.townymission.utils.RankUtil;

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

    public Integer getSprintRank(UUID townUUID) {
        return getRank(townUUID, true);
    }

    public Integer getSeasonRank(UUID townUUID) {
        return getRank(townUUID, false);
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
