package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.entity.EntityType;

/**
 * The type Mob.
 */
public class Mob extends MissionJson {
    private final EntityType entityType;

    /**
     * Instantiates a new Mob.
     *
     * @param entityType the entity type
     * @param amount     the amount
     * @param completed  the completed
     */
    public Mob(EntityType entityType, int amount, int completed, int reward) {
        super(reward, amount, completed);
        this.entityType = entityType;
    }

    /**
     * Parse mob.
     *
     * @param json the json
     * @return the mob
     */
    public static Mob parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Mob.class);
    }
}
