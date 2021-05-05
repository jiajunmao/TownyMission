package world.naturecraft.townymission.containers.json;

import com.google.gson.JsonObject;

public class Expansion extends JsonEntry {
    private final String world;
    private final int amount;
    private final int completed;

    public Expansion(String world, int amount, int completed) {
        this.world = world;
        this.amount = amount;
        this.completed = completed;
    }

    public static Expansion parse(JsonObject json) {
        return new Expansion(json.get("world").toString(),
                Integer.parseInt(json.get("amount").toString()),
                Integer.parseInt(json.get("completed").toString()));
    }
}
