package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.entity.EntityType;
import world.naturecraft.townymission.components.enums.MissionType;

import java.beans.ConstructorProperties;

/**
 * The type Mob.
 */
public class Mob extends MissionJson {

    @JsonProperty("entityType")
    private final EntityType entityType;

    /**
     * Instantiates a new Mob.
     *
     * @param entityType the entity type
     * @param amount     the amount
     * @param completed  the completed
     * @param hrAllowed  the hr allowed
     * @param reward     the reward
     */
    @ConstructorProperties({"entityType", "amount", "completed", "hrAllowed", "reward"})
    public Mob(EntityType entityType, int amount, int completed, int hrAllowed, int reward) {
        super(MissionType.MOB, amount, completed, hrAllowed, reward);
        this.entityType = entityType;
    }

    /**
     * Parse mob.
     *
     * @param json the json
     * @return the mob
     * @throws JsonProcessingException the json processing exception
     */
    public static Mob parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Mob.class);
    }

    /**
     * Gets entity type.
     *
     * @return the entity type
     */
    public EntityType getEntityType() {
        return entityType;
    }


    /**
     * Get the formatted display line when needed to be displayed in MC chat
     *
     * @return the formatted line
     */
    @Override
    @JsonIgnore
    public String getDisplayLine() {
        return "&f- &eType: &f" + getEntityType().getName() + "&7; &eAmount: &f" + getAmount() + "&7; &eReward: &f" + getReward();
    }
}
