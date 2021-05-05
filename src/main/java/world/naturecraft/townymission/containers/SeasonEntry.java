package world.naturecraft.townymission.containers;

public class SeasonEntry {
    private final int id;
    private final String townID;
    private final String townName;
    private final int seasonPoint;
    private final int season;

    public SeasonEntry(int id, String townID, String townName, int seasonPoint, int season) {
        this.id = id;
        this.townID = townID;
        this.townName = townName;
        this.seasonPoint = seasonPoint;
        this.season = season;
    }

    public int getId() {
        return id;
    }

    public String getTownID() {
        return townID;
    }

    public String getTownName() {
        return townName;
    }

    public int getSeasonPoint() {
        return seasonPoint;
    }

    public int getSeason() {
        return season;
    }
}
