package world.naturecraft.townymission.components.containers.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.components.containers.json.*;
import world.naturecraft.townymission.components.enums.MissionType;

import java.util.Locale;

/**
 * The type Task entry.
 */
public class TaskEntry {
    private final int id;
    private final String taskType;
    private final long addedTime;
    private final String town;
    private final long allowedTime;
    private long startedTime;
    private String taskJson;

    /**
     * Instantiates a new Task entry.
     *
     * @param id          the id
     * @param taskType    the task type
     * @param addedTime   the time the task is added
     * @param startedTime the started time
     * @param allowedTime the allowed time
     * @param taskJson    the task json
     * @param town        the town
     */
    public TaskEntry(int id, String taskType, long addedTime, long startedTime, long allowedTime, String taskJson, String town) {
        this.id = id;
        this.taskType = taskType;
        this.addedTime = addedTime;
        this.startedTime = startedTime;
        this.allowedTime = allowedTime;
        this.taskJson = taskJson;
        this.town = town;
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
     * Gets added time.
     *
     * @return the added time
     */
    public long getAddedTime() {
        return addedTime;
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
     * Gets display line.
     *
     * @return the display line
     * @throws JsonProcessingException the json processing exception
     */
    public String getDisplayLine() throws JsonProcessingException {
        MissionType missionType = MissionType.valueOf(taskType.toUpperCase(Locale.ROOT));
        if (missionType.equals(MissionType.EXPANSION)) {
            return Expansion.parse(taskJson).getDisplayLine();
        } else if (missionType.equals(MissionType.MOB)) {
            return Mob.parse(taskJson).getDisplayLine();
        } else if (missionType.equals(MissionType.MONEY)) {
            return Money.parse(taskJson).getDisplayLine();
        } else if (missionType.equals(MissionType.VOTE)) {
            return Vote.parse(taskJson).getDisplayLine();
        } else if (missionType.equals(MissionType.RESOURCE)) {
            return Resource.parse(taskJson).getDisplayLine();
        } else {
            return null;
        }
    }

    /**
     * Gets allowed time.
     *
     * @return the allowed time
     */
    public long getAllowedTime() {
        return allowedTime;
    }
}
