package world.naturecraft.townymission.containers;

public class SprintHistoryEntry {
    private final int id;
    private final int season;
    private final int sprint;
    private final long startedTime;
    private final String rankJson;

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
}
