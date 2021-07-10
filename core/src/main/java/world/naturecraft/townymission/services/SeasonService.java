/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.utils.RankUtil;
import world.naturecraft.townymission.components.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.json.rank.RankJson;
import world.naturecraft.townymission.components.json.rank.TownRankJson;
import world.naturecraft.townymission.data.dao.SeasonDao;
import world.naturecraft.townymission.data.dao.SeasonHistoryDao;

import java.util.List;
import java.util.UUID;

/**
 * The type Season service.
 */
public class SeasonService extends TownyMissionService {

    private static SeasonService singleton;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SeasonService getInstance() {
        if (singleton == null) {
            singleton = new SeasonService();
        }

        return singleton;
    }

    /**
     * Season end clean up.
     */
    public void seasonEndCleanUp() {
        List<TownRankJson> townRankJsons = (List<TownRankJson>) RankUtil.sort(SeasonDao.getInstance().getEntriesAsJson());
        RankJson rankJson = new RankJson(RankType.SEASON, townRankJsons);

        try {
            SeasonHistoryEntry seasonHistoryEntry =
                    new SeasonHistoryEntry(
                            UUID.randomUUID(),
                            instance.getStatsConfig().getInt("season.current"),
                            TimerService.getInstance().getStartTime(RankType.SEASON),
                            rankJson.toJson()
                    );

            SeasonHistoryDao.getInstance().add(seasonHistoryEntry);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
    }
}
