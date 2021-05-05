package world.naturecraft.townymission.containers;

public class SeasonHistoryEntry {
    private final int id;
    private final int season;
    private final long startTime;
    private final String rankJson;

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
}
