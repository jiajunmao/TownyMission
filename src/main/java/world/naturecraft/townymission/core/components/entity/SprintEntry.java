package world.naturecraft.townymission.core.components.entity;

import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.core.components.enums.DbType;

import java.util.UUID;

/**
 * The type Sprint entry.
 */
public class SprintEntry extends DataEntity implements Rankable {
    private UUID townUUID;
    private int naturepoints;
    private int sprint;
    private int season;

    /**
     * Instantiates a new Sprint entry.
     *
     * @param id           the id
     * @param townUUID     the town id
     * @param naturepoints the naturepoints
     * @param sprint       the sprint
     * @param season       the season
     */
    public SprintEntry(UUID id, UUID townUUID, int naturepoints, int sprint, int season) {
        super(id, DbType.SPRINT);
        this.townUUID = townUUID;
        this.naturepoints = naturepoints;
        this.sprint = sprint;
        this.season = season;
    }

    /**
     * Gets town id.
     *
     * @return the town id
     */
    public UUID getTownUUID() {
        return townUUID;
    }

    /**
     * Sets town id.
     *
     * @param townUUID the town id
     */
    public void setTownUUID(UUID townUUID) {
        this.townUUID = townUUID;
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
    public int getRankingFactor() {
        return naturepoints;
    }

    @Override
    public int compareTo(@NotNull Rankable rankable) {
        return naturepoints - rankable.getRankingFactor();
    }
}
