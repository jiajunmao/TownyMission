package world.naturecraft.townymission.components.containers.json;

import com.google.gson.JsonObject;
import org.bukkit.entity.EntityType;

public class Mob extends JsonEntry {
    private final EntityType entityType;
    private final int amount;
    private final int completed;

    public Mob(EntityType entityType, int amount, int completed) {
        this.entityType = entityType;
        this.amount = amount;
        this.completed = completed;
    }

    public static Mob parse(JsonObject json) {
        return new Mob(EntityType.valueOf(json.get("entityType").toString()),
                Integer.parseInt(json.get("amount").toString()),
                Integer.parseInt(json.get("completed").toString()));
    }
}
