package world.naturecraft.townymission.components.containers.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.components.containers.json.*;
import world.naturecraft.townymission.components.enums.MissionType;

import java.util.Locale;

/**
 * The type Task entry.
 */
public class TaskEntry {
    private int id;
    private final String taskType;
    private final long addedTime;
    private final long startedTime;
    private final long allowedTime;
    private final String taskJson;
    private final String town;

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

    public long getAddedTime() {
        return addedTime;
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
     * Gets started time.
     *
     * @return the started time
     */
    public long getStartedTime() {
        return startedTime;
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
     * Gets task json.
     *
     * @return the task json
     */
    public String getTaskJson() {
        return taskJson;
    }

    /**
     * Gets town.
     *
     * @return the town
     */
    public String getTown() {
        return town;
    }

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
}
