package world.naturecraft.townymission.components.containers.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.naturecraft.townymission.components.containers.json.MissionJson;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.utils.MissionJsonFactory;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.UUID;

/**
 * The type Task entry.
 */
public class MissionEntry extends SqlEntry {
    private final MissionType missionType;
    private final long addedTime;
    private final long allowedTime;
    private final Town town;
    private long startedTime;
    private MissionJson missionJson;
    private Player startedPlayer;

    /**
     * Instantiates a new Task entry.
     *
     * @param id            the id
     * @param missionType   the task type
     * @param addedTime     the time the task is added
     * @param startedTime   the started time
     * @param allowedTime   the allowed time
     * @param missionJson   the task json
     * @param town          the town
     * @param startedPlayer the started player
     */
    public MissionEntry(int id, MissionType missionType, long addedTime, long startedTime, long allowedTime, MissionJson missionJson, Town town, Player startedPlayer) {
        super(id, DbType.MISSION);
        this.missionType = missionType;
        this.addedTime = addedTime;
        this.startedTime = startedTime;
        this.allowedTime = allowedTime;
        this.missionJson = missionJson;
        this.town = town;
        this.startedPlayer = startedPlayer;
    }

    /**
     * Instantiates a new Task entry.
     *
     * @param id                the id
     * @param missionType       the mission type
     * @param addedTime         the added time
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the mission json
     * @param townName          the town name
     * @param startedPlayerUUID the started player name
     * @throws JsonProcessingException the json processing exception
     */
    public MissionEntry(int id, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, String townName, String startedPlayerUUID) throws JsonProcessingException {
        this(id, MissionType.valueOf(missionType), addedTime, startedTime, allowedTime, null, TownyUtil.getTownByName(townName), (startedPlayerUUID == null || startedPlayerUUID.equals("null")) ? null : Bukkit.getPlayer(UUID.fromString(startedPlayerUUID)));

        //TODO: replace with polymorphism
        this.missionJson = MissionJsonFactory.getJson(missionJson, MissionType.valueOf(missionType));
    }

    /**
     * Gets added time.
     *
     * @return the added time
     */
    public long getAddedTime() {
        return addedTime;
    }

    /**
     * Gets task type.
     *
     * @return the task type
     */
    public MissionType getMissionType() {
        return missionType;
    }

    /**
     * Gets started time.
     *
     * @return the started time
     */
    public long getStartedTime() {
        return startedTime;
    }

    /**
     * Sets started time.
     *
     * @param startedTime the started time
     */
    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

    /**
     * Gets task json.
     *
     * @return the task json
     */
    public MissionJson getMissionJson() {
        return missionJson;
    }

    /**
     * Sets mission json.
     *
     * @param json the json
     * @throws JsonProcessingException the json processing exception
     */
    public void setMissionJson(MissionJson json) throws JsonProcessingException {
        this.missionJson = json;
    }

    /**
     * Gets town.
     *
     * @return the town
     */
    public Town getTown() {
        return town;
    }

    /**
     * Gets display line.
     *
     * @return the display line
     * @throws JsonProcessingException the json processing exception
     */
    public String getDisplayLine() throws JsonProcessingException {
        return missionJson.getDisplayLine();
    }

    /**
     * Gets allowed time.
     *
     * @return the allowed time
     */
    public long getAllowedTime() {
        return allowedTime;
    }

    /**
     * Gets started player.
     *
     * @return the started player
     */
    public Player getStartedPlayer() {
        return startedPlayer;
    }

    /**
     * Sets started player.
     *
     * @param startedPlayer the started player
     */
    public void setStartedPlayer(Player startedPlayer) {
        this.startedPlayer = startedPlayer;
    }

    private Material getGuiItemMaterial() {
        switch (missionType) {
            case RESOURCE:
                return Material.WHEAT;
            case MOB:
                return Material.ZOMBIE_HEAD;
            case EXPANSION:
                return Material.GRASS_PATH;
            case VOTE:
                return Material.WRITABLE_BOOK;
            case MONEY:
                return Material.PAPER;
            default:
                return null;
        }
    }

    /**
     * Gets gui item.
     *
     * @return the gui item
     */
    public ItemStack getGuiItem() {
        ItemStack stack = new ItemStack(getGuiItemMaterial(), 1);

        ItemMeta meta = stack.getItemMeta();

        String displayName = "&r&6&l" + Util.capitalizeFirst(missionType.name()) + " Mission";
        meta.setDisplayName(Util.translateColor(displayName));
        meta.setLore(missionJson.getLore());

        if (startedTime != 0) {
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        stack.setItemMeta(meta);
        return stack;
    }
}
