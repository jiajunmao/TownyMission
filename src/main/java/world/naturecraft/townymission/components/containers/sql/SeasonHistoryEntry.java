package world.naturecraft.townymission.components.containers.sql;

import world.naturecraft.townymission.components.enums.DbType;

/**
 * The type Season history entry.
 */
public class SeasonHistoryEntry extends SqlEntry {
    private int id;
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
    public SeasonHistoryEntry(int id, int season, long startTime, String rankJson) {
        super(id, DbType.SEASON_HISTORY);
        this.season = season;
        this.startTime = startTime;
        this.rankJson = rankJson;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
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
