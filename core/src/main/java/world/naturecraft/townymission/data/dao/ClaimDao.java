package world.naturecraft.townymission.data.dao;

import world.naturecraft.naturelib.database.Dao;
import world.naturecraft.townymission.components.entity.ClaimEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.RewardType;
import world.naturecraft.townymission.data.storage.ClaimStorage;
import world.naturecraft.townymission.services.StorageService;

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
        db.add(data.getPlayerUUID(),
                data.getRewardType().name(),
                data.getRewardJson().toJson(),
                data.getSeason(),
                data.getSprint());
    }

    /**
     * Update.
     *
     * @param data the data
     */
    @Override
    public void update(ClaimEntry data) {
        db.update(data.getId(), data.getPlayerUUID(), data.getRewardType().name(),
                data.getRewardJson().toJson(), data.getSeason(), data.getSprint());
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

    public void reloadDb() {
        singleton = new ClaimDao();
    }

    public void addAndMerge(ClaimEntry entry) {
        List<ClaimEntry> compatibleList = ClaimDao.getInstance().getEntries(data -> data.getRewardType().equals(entry.getRewardType())
                && entry.getRewardType() != RewardType.COMMAND
                && data.getPlayerUUID().equals(entry.getPlayerUUID()));

        if (compatibleList.isEmpty()) {
            ClaimDao.getInstance().add(entry);
        } else {
            ClaimEntry claimEntry = compatibleList.get(0);
            claimEntry.getRewardJson().setAmount(claimEntry.getRewardJson().getAmount() + entry.getRewardJson().getAmount());
            ClaimDao.getInstance().update(claimEntry);
        }
    }
}
