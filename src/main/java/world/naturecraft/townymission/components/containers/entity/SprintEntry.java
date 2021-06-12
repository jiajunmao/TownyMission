package world.naturecraft.townymission.components.containers.entity;

import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.UUID;

/**
 * The type Sprint entry.
 */
public class SprintEntry extends DataEntity implements Rankable {
    private String townID;
    private String townName;
    private int naturepoints;
    private int sprint;
    private int season;

    /**
     * Instantiates a new Sprint entry.
     *
     * @param id           the id
     * @param townID       the town id
     * @param townName     the town name
     * @param naturepoints the naturepoints
     * @param sprint       the sprint
     * @param season       the season
     */
    public SprintEntry(UUID id, String townID, String townName, int naturepoints, int sprint, int season) {
        super(id, DbType.SPRINT);
        this.townID = townID;
        this.townName = townName;
        this.naturepoints = naturepoints;
        this.sprint = sprint;
        this.season = season;
    }

    /**
     * Gets town id.
     *
     * @return the town id
     */
    public String getTownID() {
        return townID;
    }

    /**
     * Sets town id.
     *
     * @param townID the town id
     */
    public void setTownID(String townID) {
        this.townID = townID;
    }

    /**
     * Gets town name.
     *
     * @return the town name
     */
    public String getTownName() {
        return townName;
    }

    /**
     * Sets town name.
     *
     * @param townName the town name
     */
    public void setTownName(String townName) {
        this.townName = townName;
    }

    /**
     * Gets naturepoints.
     *
     * @return the naturepoints
     */
    public int getNaturepoints() {
        return naturepoints;
    }

    /**
     * Sets naturepoints.
     *
     * @param naturepoints the naturepoints
     */
    public void setNaturepoints(int naturepoints) {
        this.naturepoints = naturepoints;
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

    @Override
    public int getPoint() {
        return naturepoints;
    }

    @Override
    public String getID() {
        return townID;
    }

    @Override
    public int compareTo(@NotNull Rankable rankable) {
        return naturepoints - rankable.getPoint();
    }
}
