package world.naturecraft.townymission.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.api.exceptions.DataProcessException;
import world.naturecraft.townymission.components.entity.ClaimEntry;
import world.naturecraft.townymission.data.db.ClaimStorage;

/**
 * The type Claim dao.
 */
public class ClaimDao extends Dao<ClaimEntry> {

    private static ClaimDao singleton;
    private final ClaimStorage db;

    /**
     * Instantiates a new Claim dao.
     */
    public ClaimDao() {
        super(ClaimStorage.getInstance());
        db = ClaimStorage.getInstance();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ClaimDao getInstance() {
        if (singleton == null) {
            singleton = new ClaimDao();
        }

        return singleton;
    }

    /**
     * Add.
     *
     * @param data the data
     */
    @Override
    public void add(ClaimEntry data) {
        try {
            db.add(data.getPlayerUUID().toString(),
                    data.getRewardJson().toJson(),
                    data.getSeason(),
                    data.getSprint());
        } catch (JsonProcessingException e) {
            throw new DataProcessException(e);
        }
    }

    /**
     * Update.
     *
     * @param data the data
     */
    @Override
    public void update(ClaimEntry data) {
        try {
            db.update(data.getId(), data.getPlayerUUID().toString(),
                    data.getRewardJson().toJson(), data.getSeason(), data.getSprint());
        } catch (JsonProcessingException e) {
            throw new DataProcessException(e);
        }
    }

    /**
     * Remove.
     *
     * @param data the data
     */
    @Override
    public void remove(ClaimEntry data) {
        db.remove(data.getId());
    }
}
