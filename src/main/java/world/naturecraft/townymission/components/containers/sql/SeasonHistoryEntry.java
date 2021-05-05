package world.naturecraft.townymission.components.containers.sql;

public class SeasonHistoryEntry {
    private int id;
    private int season;
    private long startTime;
    private String rankJson;

    public SeasonHistoryEntry(int id, int season, long startTime, String rankJson) {
        this.id = id;
        this.season = season;
        this.startTime = startTime;
        this.rankJson = rankJson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getRankJson() {
        return rankJson;
    }

    public void setRankJson(String rankJson) {
        this.rankJson = rankJson;
    }
}
