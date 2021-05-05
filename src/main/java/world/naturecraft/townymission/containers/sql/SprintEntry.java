package world.naturecraft.townymission.containers;

public class SprintEntry {
    private final int id;
    private final String townID;
    private final String townName;
    private final int naturepoints;
    private final int sprint;
    private final int season;

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

    public String getTownID() {
        return townID;
    }

    public String getTownName() {
        return townName;
    }

    public int getNaturepoints() {
        return naturepoints;
    }

    public int getSprint() {
        return sprint;
    }

    public int getSeason() {
        return season;
    }
}
