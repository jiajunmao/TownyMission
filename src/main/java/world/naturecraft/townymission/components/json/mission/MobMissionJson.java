package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.entity.EntityType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.Util;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Mob.
 */
public class MobJson extends MissionJson {

    @JsonProperty("entityType")
    private final EntityType entityType;

    /**
     * Instantiates a new Mob.
     *
     * @param entityType    the entity type
     * @param amount        the amount
     * @param completed     the completed
     * @param hrAllowed     the hr allowed
     * @param reward        the reward
     * @param contributions the contributions
     */
    @ConstructorProperties({"entityType", "amount", "completed", "hrAllowed", "reward", "contributions"})
    public MobJson(EntityType entityType, int amount, int completed, int hrAllowed, int reward, Map<String, Integer> contributions) {
        super(MissionType.MOB, amount, completed, hrAllowed, reward, contributions);
        this.entityType = entityType;
    }

    /**
     * Parse mob.
     *
     * @param json the json
     * @return the mob
     * @throws JsonProcessingException the json processing exception
     */
    public static MissionJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, MobJson.class);
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

    /**
     * Get the item lore if using GUI
     *
     * @return The lore in a list of string
     */
    @Override
    @JsonIgnore
    public List<String> getLore() {
        List<String> loreList = new ArrayList<>();
        loreList.add(Util.translateColor("&r&eMob Type: &7" + Util.capitalizeFirst(entityType.name())));
        loreList.add(Util.translateColor("&r&eAmount: &7" + getAmount()));
        loreList.add(Util.translateColor("&r&eReward: &7" + getReward()));
        loreList.add(Util.translateColor("&r&eAllowed Time: &7" + getHrAllowed() + "hr"));

        return loreList;
    }
}
