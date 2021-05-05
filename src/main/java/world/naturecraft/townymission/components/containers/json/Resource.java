package world.naturecraft.townymission.components.containers.json;

import com.google.gson.JsonObject;
import org.bukkit.Material;

/**
 * The type Resource.
 */
public class Resource extends JsonEntry {

    private final boolean isMi;
    private final Material type;
    private final int amount;
    private final int completed;

    /**
     * Instantiates a new Resource.
     *
     * @param isMi      the is mi
     * @param type      the type
     * @param amount    the amount
     * @param completed the completed
     */
    public Resource(boolean isMi, Material type, int amount, int completed) {
        this.isMi = isMi;
        this.type = type;
        this.amount = amount;
        this.completed = completed;
    }

    /**
     * Parse resource.
     *
     * @param json the json
     * @return the resource
     */
    public static Resource parse(JsonObject json) {
        return new Resource(
                Boolean.parseBoolean(json.get("isMi").toString()),
                Material.valueOf(json.get("type").toString()),
                Integer.parseInt(json.get("amount").toString()),
                Integer.parseInt(json.get("completed").toString()));
    }
}
