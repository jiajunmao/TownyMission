package world.naturecraft.townymission.containers.sql;

public class TaskEntry {
    private int id;
    private String taskType;
    private long startedTime;
    private long allowedTime;
    private String taskJson;
    private String town;

    public TaskEntry(int id, String taskType, long startedTime, long allowedTime, String taskJson, String town) {
        this.id = id;
        this.taskType = taskType;
        this.startedTime = startedTime;
        this.allowedTime = allowedTime;
        this.taskJson = taskJson;
        this.town = town;
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

    public void setId(int id) {
        this.id = id;
    }
}
