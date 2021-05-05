package world.naturecraft.townymission.components.containers.json;

import com.google.gson.JsonObject;

/**
 * The type Vote.
 */
public class Vote extends JsonEntry {

    private final int amount;
    private final int completed;

    /**
     * Instantiates a new Vote.
     *
     * @param amount    the amount
     * @param completed the completed
     */
    public Vote(int amount, int completed) {
        this.amount = amount;
        this.completed = completed;
    }

    /**
     * Parse vote.
     *
     * @param json the json
     * @return the vote
     */
    public static Vote parse(JsonObject json) {
        return new Vote(
                Integer.parseInt(json.get("amount").toString()),
                Integer.parseInt(json.get("completed").toString()));
    }
}
