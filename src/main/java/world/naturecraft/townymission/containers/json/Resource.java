package world.naturecraft.townymission.containers.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import org.bukkit.Material;

public class Resource extends JsonEntry {

    private final boolean isMi;
    private final Material type;
    private final int amount;
    private final int completed;

    public Resource(boolean isMi, Material type, int amount, int completed) {
        this.isMi = isMi;
        this.type = type;
        this.amount = amount;
        this.completed = completed;
    }

    public static Resource parse(JsonObject json) {
        return new Resource(
                Boolean.parseBoolean(json.get("isMi").toString()),
                Material.valueOf(json.get("type").toString()),
                Integer.parseInt(json.get("amount").toString()),
                Integer.parseInt(json.get("completed").toString()));
    }
}
