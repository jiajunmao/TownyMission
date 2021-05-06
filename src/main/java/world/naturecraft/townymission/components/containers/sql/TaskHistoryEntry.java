package world.naturecraft.townymission.components.containers.sql;

/**
 * The type Task history entry.
 */
public class TaskHistoryEntry {

    private int id;
    private String taskType;
    private long addedTime;
    private long startedTime;
    private long allowedTime;
    private String taskJson;
    private String town;
    private long completedTime;
    private int sprint;
    private int season;

    /**
     * Instantiates a new Task history entry.
     *
     * @param id            the id
     * @param taskType      the task type
     * @param startedTime   the started time
     * @param allowedTime   the allowed time
     * @param taskJson      the task json
     * @param town          the town
     * @param completedTime the completed time
     * @param sprint        the sprint
     * @param season        the season
     */
    public TaskHistoryEntry(int id, String taskType, long addedTime, long startedTime, long allowedTime, String taskJson, String town, long completedTime, int sprint, int season) {
        this.id = id;
        this.taskType = taskType;
        this.addedTime = addedTime;
        this.startedTime = startedTime;
        this.allowedTime = allowedTime;
        this.taskJson = taskJson;
        this.town = town;
        this.completedTime = completedTime;
        this.sprint = sprint;
        this.season = season;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets task type.
     *
     * @return the task type
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * Sets task type.
     *
     * @param taskType the task type
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    /**
     * Gets started time.
     *
     * @return the started time
     */
    public long getStartedTime() {
        return startedTime;
    }

    /**
     * Sets started time.
     *
     * @param startedTime the started time
     */
    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

    /**
     * Gets allowed time.
     *
     * @return the allowed time
     */
    public long getAllowedTime() {
        return allowedTime;
    }

    /**
     * Sets allowed time.
     *
     * @param allowedTime the allowed time
     */
    public void setAllowedTime(long allowedTime) {
        this.allowedTime = allowedTime;
    }

    /**
     * Gets task json.
     *
     * @return the task json
     */
    public String getTaskJson() {
        return taskJson;
    }

    /**
     * Sets task json.
     *
     * @param taskJson the task json
     */
    public void setTaskJson(String taskJson) {
        this.taskJson = taskJson;
    }

    /**
     * Gets town.
     *
     * @return the town
     */
    public String getTown() {
        return town;
    }

    /**
     * Sets town.
     *
     * @param town the town
     */
    public void setTown(String town) {
        this.town = town;
    }

    /**
     * Gets completed time.
     *
     * @return the completed time
     */
    public long getCompletedTime() {
        return completedTime;
    }

    /**
     * Sets completed time.
     *
     * @param completedTime the completed time
     */
    public void setCompletedTime(long completedTime) {
        this.completedTime = completedTime;
    }

    /**
     * Gets sprint.
     *
     * @return the sprint
     */
    public int getSprint() {
        return sprint;
    }

    /**
     * Sets sprint.
     *
     * @param sprint the sprint
     */
    public void setSprint(int sprint) {
        this.sprint = sprint;
    }

    /**
     * Gets season.
     *
     * @return the season
     */
    public int getSeason() {
        return season;
    }

    /**
     * Sets season.
     *
     * @param season the season
     */
    public void setSeason(int season) {
        this.season = season;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(long addedTime) {
        this.addedTime = addedTime;
    }
}
