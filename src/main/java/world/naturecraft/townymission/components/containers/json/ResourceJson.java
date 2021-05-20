package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import world.naturecraft.townymission.components.enums.MissionType;

import java.beans.ConstructorProperties;
import java.util.Map;

/**
 * The type Resource.
 */
public class ResourceJson extends MissionJson {

    @JsonProperty("mi")
    private final boolean isMi;
    @JsonProperty("type")
    private final Material type;

    /**
     * Instantiates a new Resource.
     *
     * @param isMi      the is mi
     * @param type      the type
     * @param amount    the amount
     * @param completed the completed
     * @param hrAllowed the hr allowed
     * @param reward    the reward
     */
    @ConstructorProperties({"mi", "type", "amount", "completed", "hrAllowed", "reward", "contributions"})
    public ResourceJson(boolean isMi, Material type, int amount, int completed, int hrAllowed, int reward, Map<String, Integer> contributions) {
        super(MissionType.RESOURCE, amount, completed, hrAllowed, reward, contributions);
        this.isMi = isMi;
        this.type = type;
    }

    /**
     * Parse resource.
     *
     * @param json the json
     * @return the resource
     * @throws JsonProcessingException the json processing exception
     */
    public static ResourceJson parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, ResourceJson.class);
    }

    /**
     * Is mi boolean.
     *
     * @return the boolean
     */
    public boolean isMi() {
        return isMi;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Material getType() {
        return type;
    }

    /**
     * Get the formatted display line when needed to be displayed in MC chat
     *
     * @return the formatted line
     */
    @Override
    @JsonIgnore
    public String getDisplayLine() {
        return "&f- &eisMi: &f" + isMi() + "&7; &e Type: &f" + getType().name() + "&7; &eAmount: &f" + getAmount() + "&7; &eReward: &f" + getReward();
    }
}
