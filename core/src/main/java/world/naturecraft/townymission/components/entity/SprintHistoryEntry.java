package world.naturecraft.townymission.components.entity;

import world.naturecraft.naturelib.components.DataEntity;

import java.util.UUID;

/**
 * The type Sprint history entry.
 */
public class SprintHistoryEntry extends DataEntity {
    private int season;
    private int sprint;
    private long startedTime;
    private String rankJson;

    /**
     * Instantiates a new Sprint history entry.
     *
     * @param id          the id
     * @param season      the season
     * @param sprint      the sprint
     * @param startedTime the started time
     * @param rankJson    the rank json
     */
    public SprintHistoryEntry(UUID id, int season, int sprint, long startedTime, String rankJson) {
        super(id);
        this.season = season;
        this.sprint = sprint;
        this.startedTime = startedTime;
        this.rankJson = rankJson;
    }

    /**
     * Gets started time.
     *
     * @return the started time
     */
    public long getStartedTime() {
        return startedTime;
    }

    /**
     * Sets started time.
     *
     * @param startedTime the started time
     */
    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
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
     * Gets sprint.
     *
     * @return the sprint
     */
    public int getSprint() {
        return sprint;
    }

    /**
     * Sets sprint.
     *
     * @param sprint the sprint
     */
    public void setSprint(int sprint) {
        this.sprint = sprint;
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
