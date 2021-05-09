package world.naturecraft.townymission.components.containers.sql;

/**
 * The type Season entry.
 */
public class SeasonEntry {
    private int id;
    private String townID;
    private String townName;
    private int seasonPoint;
    private int season;

    /**
     * Instantiates a new Season entry.
     *
     * @param id          the id
     * @param townID      the town id
     * @param townName    the town name
     * @param seasonPoint the season point
     * @param season      the season
     */
    public SeasonEntry(int id, String townID, String townName, int seasonPoint, int season) {
        this.id = id;
        this.townID = townID;
        this.townName = townName;
        this.seasonPoint = seasonPoint;
        this.season = season;
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
}
