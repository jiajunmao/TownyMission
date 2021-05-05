package world.naturecraft.townymission.containers.sql;

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

    public int getSeason() {
        return season;
    }

    public long getStartTime() {
        return startTime;
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

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setRankJson(String rankJson) {
        this.rankJson = rankJson;
    }
}
