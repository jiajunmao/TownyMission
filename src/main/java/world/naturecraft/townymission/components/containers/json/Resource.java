package world.naturecraft.townymission.components.containers.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;

/**
 * The type Resource.
 */
public class Resource extends MissionJson {

    private final boolean isMi;
    private final Material type;

    /**
     * Instantiates a new Resource.
     *
     * @param isMi      the is mi
     * @param type      the type
     * @param amount    the amount
     * @param completed the completed
     */
    public Resource(boolean isMi, Material type, int amount, int completed, int reward) {
        super(amount, completed, reward);
        this.isMi = isMi;
        this.type = type;
    }

    /**
     * Parse resource.
     *
     * @param json the json
     * @return the resource
     */
    public static Resource parse(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Resource.class);
    }
}
