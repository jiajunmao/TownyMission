package world.naturecraft.townymission.components.json.reward;

import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.entity.Rankable;
import world.naturecraft.townymission.utils.Util;


// Well technically this is not a json, but I guess I will put it here
public class SeasonPointRewardJson implements Rankable {

    private boolean isOthers;
    private int rank;
    private int amount;

    public SeasonPointRewardJson(String rewardString) {
        int middleIdx = rewardString.indexOf(":");
        isOthers = (rewardString.substring(0, middleIdx).equalsIgnoreCase("others"));
        if (!isOthers) {
            rank = Integer.parseInt(rewardString.substring(0, middleIdx));
        }
        amount = Integer.parseInt(rewardString.substring(middleIdx+1));
    }

    public boolean isOthers() {
        return isOthers;
    }

    public int getRank() {
        return rank;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * Gets point.
     *
     * @return the point
     */
    @Override
    public int getPoint() {
        return rank;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Override
    public String getID() {
        // This should not be called
        return null;
    }

    @Override
    public int compareTo(@NotNull Rankable rankable) {
        return rank - rankable.getPoint();
    }
}
