package world.naturecraft.townymission.components.entity;

import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.enums.DbType;

import java.util.UUID;

/**
 * The type Season entry.
 */
public class SeasonEntry extends DataEntity implements Rankable {
    private UUID townUUID;
    private int seasonPoint;
    private int season;

    /**
     * Instantiates a new Season entry.
     *
     * @param id          the id
     * @param townUUID    the town id
     * @param seasonPoint the season point
     * @param season      the season
     */
    public SeasonEntry(UUID id, UUID townUUID, int seasonPoint, int season) {
        super(id, DbType.SEASON);
        this.townUUID = townUUID;
        this.seasonPoint = seasonPoint;
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
     * Gets season point.
     *
     * @return the season point
     */
    public int getSeasonPoint() {
        return seasonPoint;
    }

    /**
     * Sets season point.
     *
     * @param seasonPoint the season point
     */
    public void setSeasonPoint(int seasonPoint) {
        this.seasonPoint = seasonPoint;
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
    public String getRankingId() { return townUUID.toString(); }

    @Override
    public int getRankingFactor() {
        return getSeasonPoint();
    }


    @Override
    public int compareTo(@NotNull Rankable rankable) {
        return this.getRankingFactor() - rankable.getRankingFactor();
    }
}
