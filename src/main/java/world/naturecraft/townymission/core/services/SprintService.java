/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMissionBukkit;
import world.naturecraft.townymission.bukkit.utils.RankUtil;
import world.naturecraft.townymission.core.components.entity.SprintEntry;
import world.naturecraft.townymission.core.components.entity.SprintHistoryEntry;
import world.naturecraft.townymission.core.components.enums.RankType;
import world.naturecraft.townymission.core.components.json.rank.RankJson;
import world.naturecraft.townymission.core.components.json.rank.TownRankJson;
import world.naturecraft.townymission.core.data.dao.SprintDao;
import world.naturecraft.townymission.core.data.dao.SprintHistoryDao;

import java.util.List;
import java.util.UUID;

/**
 * The type Sprint service.
 */
public class SprintService {

    private static SprintService singleton;
    private final TownyMissionBukkit instance;

    /**
     * Instantiates a new Sprint service.
     *
     * @param instance the instance
     */
    public SprintService(TownyMissionBukkit instance) {
        this.instance = instance;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SprintService getInstance() {
        if (singleton == null) {
            TownyMissionBukkit instance = (TownyMissionBukkit) Bukkit.getPluginManager().getPlugin("TownyMission");
            singleton = new SprintService(instance);
        }

        return singleton;
    }

    /**
     * Sprint end clean up.
     */
    public void sprintEndCleanUp() {
        List<TownRankJson> townRankJsons = (List<TownRankJson>) RankUtil.sort(SprintDao.getInstance().getEntriesAsJson());
        RankJson rankJson = new RankJson(RankType.SPRINT, townRankJsons);

        try {
            SprintHistoryEntry sprintHistoryEntry =
                    new SprintHistoryEntry(
                            UUID.randomUUID(),
                            instance.getStatsConfig().getInt("season.current"),
                            instance.getStatsConfig().getInt("sprint.current"),
                            TimerService.getInstance().getStartTime(RankType.SPRINT),
                            rankJson.toJson());

            SprintHistoryDao.getInstance().add(sprintHistoryEntry);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }

        for (SprintEntry sprintEntry : SprintDao.getInstance().getEntries()) {
            SprintDao.getInstance().remove(sprintEntry);
        }
    }
}
