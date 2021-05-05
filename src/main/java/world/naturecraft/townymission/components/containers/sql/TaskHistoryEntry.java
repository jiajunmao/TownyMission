package world.naturecraft.townymission.components.containers.sql;

public class TaskHistoryEntry {

    private int id;
    private String taskType;
    private long startedTime;
    private long allowedTime;
    private String taskJson;
    private String town;
    private long completedTime;
    private int sprint;
    private int season;

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

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

    public long getAllowedTime() {
        return allowedTime;
    }

    public void setAllowedTime(long allowedTime) {
        this.allowedTime = allowedTime;
    }

    public String getTaskJson() {
        return taskJson;
    }

    public void setTaskJson(String taskJson) {
        this.taskJson = taskJson;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public long getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(long completedTime) {
        this.completedTime = completedTime;
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
