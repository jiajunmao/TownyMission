package world.naturecraft.townymission.components.containers.sql;

public class TaskEntry {
    private int id;
    private final String taskType;
    private final long startedTime;
    private final long allowedTime;
    private final String taskJson;
    private final String town;

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

    public void setId(int id) {
        this.id = id;
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
}
