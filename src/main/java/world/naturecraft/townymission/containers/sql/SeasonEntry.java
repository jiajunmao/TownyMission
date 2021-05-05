package world.naturecraft.townymission.containers.sql;

public class SeasonEntry {
    private int id;
    private String townID;
    private String townName;
    private int seasonPoint;
    private int season;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setTownID(String townID) {
        this.townID = townID;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public void setSeasonPoint(int seasonPoint) {
        this.seasonPoint = seasonPoint;
    }

    public void setSeason(int season) {
        this.season = season;
    }
}
