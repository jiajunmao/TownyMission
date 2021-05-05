package world.naturecraft.townymission.components.containers.json;

import com.google.gson.JsonObject;

/**
 * The type Money.
 */
public class Money extends JsonEntry {
    private final int required;
    private final int completed;

    /**
     * Instantiates a new Money.
     *
     * @param required  the required
     * @param completed the completed
     */
    public Money(int required, int completed) {
        this.required = required;
        this.completed = completed;
    }

    /**
     * Parse money.
     *
     * @param json the json
     * @return the money
     */
    public static Money parse(JsonObject json) {
        return new Money(
                Integer.parseInt(json.get("required").toString()),
                Integer.parseInt(json.get("completed").toString())
        );
    }
}
