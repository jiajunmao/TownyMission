package world.naturecraft.townymission.components.containers.json;

import com.google.gson.JsonObject;
import org.bukkit.entity.EntityType;

/**
 * The type Mob.
 */
public class Mob extends JsonEntry {
    private final EntityType entityType;
    private final int amount;
    private final int completed;

    /**
     * Instantiates a new Mob.
     *
     * @param entityType the entity type
     * @param amount     the amount
     * @param completed  the completed
     */
    public Mob(EntityType entityType, int amount, int completed) {
        this.entityType = entityType;
        this.amount = amount;
        this.completed = completed;
    }

    /**
     * Parse mob.
     *
     * @param json the json
     * @return the mob
     */
    public static Mob parse(JsonObject json) {
        return new Mob(EntityType.valueOf(json.get("entityType").toString()),
                Integer.parseInt(json.get("amount").toString()),
                Integer.parseInt(json.get("completed").toString()));
    }
}
