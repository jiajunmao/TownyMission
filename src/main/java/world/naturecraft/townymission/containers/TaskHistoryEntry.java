package world.naturecraft.townymission.containers;

public class TaskHistoryEntry {
    private final int id;
    private final String taskType;
    private final long startedTime;
    private final long allowedTime;
    private final String taskJson;
    private final String town;
    private final long completedTime;
    private final int sprint;
    private final int season;

    public TaskHistoryEntry(int id, String taskType, long startedTime, long allowedTime, String taskJson, String town, long completedTime, int sprint, int season) {
        this.id = id;
        this.taskType = taskType;
        this.startedTime = startedTime;
        this.allowedTime = allowedTime;
        this.taskJson = taskJson;
        this.town = town;
        this.completedTime = completedTime;
        this.sprint = sprint;
        this.season = season;
    }

    public int getId() {
        return id;
    }

    public String getTaskType() {
        return taskType;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public long getAllowedTime() {
        return allowedTime;
    }

    public String getTaskJson() {
        return taskJson;
    }

    public String getTown() {
        return town;
    }

    public long getCompletedTime() {
        return completedTime;
    }

    public int getSprint() {
        return sprint;
    }

    public int getSeason() {
        return season;
    }
}
