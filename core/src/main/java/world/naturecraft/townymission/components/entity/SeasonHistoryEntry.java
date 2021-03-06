package world.naturecraft.townymission.components.entity;

import world.naturecraft.naturelib.components.DataEntity;

import java.util.UUID;

/**
 * The type Season history entry.
 */
public class SeasonHistoryEntry extends DataEntity {
    private int season;
    private long startTime;
    private String rankJson;

    /**
     * Instantiates a new Season history entry.
     *
     * @param id        the id
     * @param season    the season
     * @param startTime the start time
     * @param rankJson  the rank json
     */
    public SeasonHistoryEntry(UUID id, int season, long startTime, String rankJson) {
        super(id);
        this.season = season;
        this.startTime = startTime;
        this.rankJson = rankJson;
    }

    /**
     * Gets season.
     *
     * @return the season
     */
    public int getSeason() {
        return season;
    }

    /**
     * Sets season.
     *
     * @param season the season
     */
    public void setSeason(int season) {
        this.season = season;
    }

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets rank json.
     *
     * @return the rank json
     */
    public String getRankJson() {
        return rankJson;
    }

    /**
     * Sets rank json.
     *
     * @param rankJson the rank json
     */
    public void setRankJson(String rankJson) {
        this.rankJson = rankJson;
    }
}
