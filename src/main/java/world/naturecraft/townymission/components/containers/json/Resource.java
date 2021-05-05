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

    public boolean isMi() {
        return isMi;
    }

    public Material getType() {
        return type;
    }

    /**
     * Get the formatted display line when needed to be displayed in MC chat
     *
     * @return the formatted line
     */
    @Override
    public String getDisplayLine() {
        return "&f- &eisMi: &f" + isMi() + "&7; &e Type: &f" + getType().name() + "&7; &eAmount: &f" + getAmount() + "&7; &eReward: &f" + getReward();
    }
}
