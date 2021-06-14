/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.entity.SeasonHistoryEntry;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.json.rank.RankJson;
import world.naturecraft.townymission.components.json.rank.TownRankJson;
import world.naturecraft.townymission.data.dao.SeasonDao;
import world.naturecraft.townymission.data.dao.SeasonHistoryDao;
import world.naturecraft.townymission.utils.RankUtil;

import java.util.List;
import java.util.UUID;

/**
 * The type Season service.
 */
public class SeasonService {

    private static SeasonService singleton;
    private TownyMission instance;

    public SeasonService(TownyMission instance) {
        this.instance = instance;
    }

    public static SeasonService getInstance() {
        if (singleton == null) {
            TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
            singleton = new SeasonService(instance);
        }

        return singleton;
    }

    public void seasonEndCleanUp() {
        List<TownRankJson> townRankJsons = (List<TownRankJson>) RankUtil.sort(SeasonDao.getInstance().getEntriesAsJson());
        RankJson rankJson = new RankJson(RankType.SEASON, townRankJsons);

        try {
            SeasonHistoryEntry seasonHistoryEntry =
                    new SeasonHistoryEntry(
                            UUID.randomUUID(),
                            instance.getConfig().getInt("season.current"),
                            TimerService.getInstance().getStartTime(RankType.SEASON),
                            rankJson.toJson()
                    );

            SeasonHistoryDao.getInstance().add(seasonHistoryEntry);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
    }
}
