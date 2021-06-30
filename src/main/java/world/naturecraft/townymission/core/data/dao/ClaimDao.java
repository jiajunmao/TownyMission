package world.naturecraft.townymission.core.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import world.naturecraft.townymission.bukkit.api.exceptions.DataProcessException;
import world.naturecraft.townymission.core.components.entity.ClaimEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.enums.RewardType;
import world.naturecraft.townymission.core.data.db.ClaimStorage;
import world.naturecraft.townymission.core.services.StorageService;
import world.naturecraft.townymission.core.utils.EntryFilter;

import java.util.List;

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
        super(StorageService.getInstance().getStorage(DbType.CLAIM));
        db = StorageService.getInstance().getStorage(DbType.CLAIM);
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
            db.add(data.getPlayerUUID(),
                    data.getRewardType().name(),
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
            db.update(data.getId(), data.getPlayerUUID(), data.getRewardType().name(),
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

    public void addAndMerge(ClaimEntry entry) {
        List<ClaimEntry> compatibleList = ClaimDao.getInstance().getEntries(new EntryFilter<ClaimEntry>() {
            @Override
            public boolean include(ClaimEntry data) {
                return data.getRewardType().equals(entry.getRewardType())
                        && entry.getRewardType() != RewardType.COMMAND
                        && data.getPlayerUUID().equals(entry.getPlayerUUID());
            }
        });

        if (compatibleList.isEmpty()) {
            ClaimDao.getInstance().add(entry);
        } else {
            ClaimEntry claimEntry = compatibleList.get(0);
            claimEntry.getRewardJson().setAmount(claimEntry.getRewardJson().getAmount() + entry.getRewardJson().getAmount());
            ClaimDao.getInstance().update(claimEntry);
        }
    }
}
