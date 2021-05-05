package world.naturecraft.townymission.containers.json;

import com.google.gson.JsonObject;

public class Vote extends JsonEntry {

    private final int amount;
    private final int completed;

    public Vote(int amount, int completed) {
        this.amount = amount;
        this.completed = completed;
    }

    public static Vote parse(JsonObject json) {
        return new Vote(
                Integer.parseInt(json.get("amount").toString()),
                Integer.parseInt(json.get("completed").toString()));
    }
}
