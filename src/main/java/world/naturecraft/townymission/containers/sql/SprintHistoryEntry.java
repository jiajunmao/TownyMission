package world.naturecraft.townymission.containers.sql;

public class SprintHistoryEntry {
    private int id;
    private int season;
    private int sprint;
    private long startedTime;
    private String rankJson;

    public SprintHistoryEntry(int id, int season, int sprint, long startedTime, String rankJson) {
        this.id = id;
        this.season = season;
        this.sprint = sprint;
        this.startedTime = startedTime;
        this.rankJson = rankJson;
    }

    public int getId() {
        return id;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public int getSeason() {
        return season;
    }

    public int getSprint() {
        return sprint;
    }

    public String getRankJson() {
        return rankJson;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public void setSprint(int sprint) {
        this.sprint = sprint;
    }

    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

    public void setRankJson(String rankJson) {
        this.rankJson = rankJson;
    }
}
