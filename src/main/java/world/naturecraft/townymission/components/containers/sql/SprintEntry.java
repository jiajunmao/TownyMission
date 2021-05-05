package world.naturecraft.townymission.components.containers.sql;

public class SprintEntry {
    private int id;
    private String townID;
    private String townName;
    private int naturepoints;
    private int sprint;
    private int season;

    public SprintEntry(int id, String townID, String townName, int naturepoints, int sprint, int season) {
        this.id = id;
        this.townID = townID;
        this.townName = townName;
        this.naturepoints = naturepoints;
        this.sprint = sprint;
        this.season = season;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTownID() {
        return townID;
    }

    public void setTownID(String townID) {
        this.townID = townID;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public int getNaturepoints() {
        return naturepoints;
    }

    public void setNaturepoints(int naturepoints) {
        this.naturepoints = naturepoints;
    }

    public int getSprint() {
        return sprint;
    }

    public void setSprint(int sprint) {
        this.sprint = sprint;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }
}
