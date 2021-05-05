package world.naturecraft.townymission.components.containers.json;

import com.google.gson.JsonObject;

public class Money extends JsonEntry {
    private final int required;
    private final int completed;

    public Money(int required, int completed) {
        this.required = required;
        this.completed = completed;
    }

    public static Money parse(JsonObject json) {
        return new Money(
                Integer.parseInt(json.get("required").toString()),
                Integer.parseInt(json.get("completed").toString())
        );
    }
}
