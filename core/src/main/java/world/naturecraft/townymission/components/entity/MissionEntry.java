package world.naturecraft.townymission.components.entity;

import com.cryptomorin.xseries.XMaterial;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.naturecraft.naturelib.components.DataEntity;
import world.naturecraft.naturelib.exceptions.ConfigParsingException;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.json.mission.MissionJson;
import world.naturecraft.townymission.components.json.mission.MobMissionJson;
import world.naturecraft.townymission.components.json.mission.ResourceMissionJson;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.ItemService;
import world.naturecraft.townymission.services.TownyMissionService;
import world.naturecraft.townymission.utils.MissionJsonFactory;
import world.naturecraft.townymission.utils.Util;

import java.util.*;

/**
 * The type Task entry.
 */
public class MissionEntry extends DataEntity {

    private final MissionType missionType;
    private final long addedTime;
    private final long allowedTime;
    private final UUID townUUID;
    private long startedTime;
    private MissionJson missionJson;
    private UUID startedPlayerUUID;
    private int numMission;

    /**
     * Instantiates a new Task entry.
     *
     * @param id                the id
     * @param missionType       the task type
     * @param addedTime         the time the task is added
     * @param startedTime       the started time
     * @param allowedTime       the allowed time
     * @param missionJson       the task json
     * @param townUUID          the town
     * @param startedPlayerUUID the started player
     * @param numMission        the slot in the mission gui
     */
    public MissionEntry(UUID id, MissionType missionType, long addedTime, long startedTime, long allowedTime, MissionJson missionJson, UUID townUUID, UUID startedPlayerUUID, int numMission) {
        super(id);
        this.missionType = missionType;
        this.addedTime = addedTime;
        this.startedTime = startedTime;
        this.allowedTime = allowedTime;
        this.missionJson = missionJson;
        this.townUUID = townUUID;
        this.startedPlayerUUID = startedPlayerUUID;
        this.numMission = numMission;
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
     * @param townUUID          the town name
     * @param startedPlayerUUID the started player name
     * @throws ConfigParsingException the json processing exception
     */
    public MissionEntry(UUID id, String missionType, long addedTime, long startedTime, long allowedTime, String missionJson, UUID townUUID, UUID startedPlayerUUID, int numMission) {
        this(id, MissionType.valueOf(missionType), addedTime, startedTime, allowedTime, null, townUUID, startedPlayerUUID, numMission);

        //TODO: replace with polymorphism
        try {
            this.missionJson = MissionJsonFactory.getJson(missionJson, MissionType.valueOf(missionType));
        } catch (JsonProcessingException e) {
            throw new ConfigParsingException(e);
        }
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
     */
    public void setMissionJson(MissionJson json) {
        this.missionJson = json;
    }

    /**
     * Gets town uuid.
     *
     * @return the town uuid
     */
    public UUID getTownUUID() {
        return townUUID;
    }

    /**
     * Gets display line.
     *
     * @return the display line
     */
    public String getDisplayLine() {
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
     * Gets started player uuid.
     *
     * @return the started player uuid
     */
    public UUID getStartedPlayerUUID() {
        return startedPlayerUUID;
    }

    /**
     * Sets started player uuid.
     *
     * @param startedPlayerUUID the started player uuid
     */
    public void setStartedPlayerUUID(UUID startedPlayerUUID) {
        this.startedPlayerUUID = startedPlayerUUID;
    }

    public int getNumMission() {
        return numMission;
    }

    public void setNumMission(int numMission) {
        this.numMission = numMission;
    }

    private Material getGuiItemMaterial() {
        TownyMissionInstance instance = TownyMissionInstance.getInstance();
        switch (missionType) {
            case RESOURCE:
                ResourceMissionJson resourceMissionJson = (ResourceMissionJson) missionJson;
                if (resourceMissionJson.isMi()) {
                    return XMaterial.valueOf(instance.getGuiLangEntry("mission_manage.missions.resource.materials.MMOItems.material")).parseMaterial();
                } else {
                    return XMaterial.valueOf(instance.getGuiLangEntry("mission_manage.missions.resource.materials.vanilla.material")).parseMaterial();
                }
            case MOB:
                MobMissionJson mobMissionJson = (MobMissionJson) missionJson;
                if (mobMissionJson.isMm()) {
                    return XMaterial.valueOf(instance.getGuiLangEntry("mission_manage.missions.mob.materials.MythicMobs.material")).parseMaterial();
                } else {
                    return XMaterial.valueOf(instance.getGuiLangEntry("mission_manage.missions.mob.materials.vanilla.material")).parseMaterial();
                }
            case EXPANSION:
                return XMaterial.DIRT_PATH.parseMaterial();
            case VOTE:
                return XMaterial.WRITABLE_BOOK.parseMaterial();
            case MONEY:
                return XMaterial.PAPER.parseMaterial();
            default:
                return null;
        }
    }

    public int getCustomModelData() {
        TownyMissionInstance instance = TownyMissionInstance.getInstance();
        switch (missionType) {
            case RESOURCE:
                ResourceMissionJson resourceMissionJson = (ResourceMissionJson) missionJson;
                if (resourceMissionJson.isMi()) {
                    return Integer.parseInt(instance.getGuiLangEntry("mission_manage.missions.resource.materials.MMOItems.CustomModelData"));
                } else {
                    return Integer.parseInt(instance.getGuiLangEntry("mission_manage.missions.resource.materials.vanilla.CustomModelData"));
                }
            case MOB:
                MobMissionJson mobMissionJson = (MobMissionJson) missionJson;
                if (mobMissionJson.isMm()) {
                    return Integer.parseInt(instance.getGuiLangEntry("mission_manage.missions.mob.materials.MythicMobs.CustomModelData"));
                } else {
                    return Integer.parseInt(instance.getGuiLangEntry("mission_manage.missions.mob.materials.vanilla.CustomModelData"));
                }
            case EXPANSION:
                return Integer.parseInt(instance.getGuiLangEntry("mission_manage.missions.expansion.CustomModelData"));
            case VOTE:
                return Integer.parseInt(instance.getGuiLangEntry("mission_manage.missions.vote.CustomModelData"));
            case MONEY:
                return Integer.parseInt(instance.getGuiLangEntry("mission_manage.missions.money.CustomModelData"));
            default:
                return 1;
        }
    }

    /**
     * Gets gui item.
     *
     * @return the gui item
     */
    public ItemStack getGuiItem() {
        ItemStack stack = new ItemStack(getGuiItemMaterial(), 1);
        if (isStarted()) {
            stack.addUnsafeEnchantment(Enchantment.LUCK, 1);
        }

        ItemMeta meta = stack.getItemMeta();

        String displayName = TownyMissionInstance.getInstance().getGuiLangEntry("mission_manage.missions." + missionType.name().toLowerCase(Locale.ROOT) + ".title");
        meta.setDisplayName(ChatService.getInstance().translateColor("&r" + displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Setting lores
        List<String> loreList = new ArrayList<>();
        String statusLine = null;
        for (String s : TownyMissionInstance.getInstance().getGuiLangEntries("mission_manage.missions." + missionType.name().toLowerCase(Locale.ROOT) + ".lores")) {
            if (s.contains("Status"))
                statusLine = s;
        }

        if (isTimedout() && !isCompleted() && isStarted()) {
            loreList.add(ChatService.getInstance().translateColor(statusLine.replace("%status%", "{#DD3322}Timed Out")));
            loreList.addAll(missionJson.getLore());
        } else if (isCompleted() && isStarted()) {
            loreList.add(ChatService.getInstance().translateColor(statusLine.replace("%status%", "&aCompleted")));
            loreList.addAll(missionJson.getLore());
        } else if (!isTimedout() && !isCompleted() && isStarted()) {
            loreList.add(ChatService.getInstance().translateColor(statusLine.replace("%status%", "{#39DBF3}Started")));
            loreList.addAll(missionJson.getStartedLore());
        } else {
            loreList.addAll(missionJson.getLore());
        }
        //loreList.add("&r&eIndex&f: " + numMission);

        meta.setLore(loreList);
        if (Util.currBukkitVer().get(1) >= 14) {
            ItemService.getInstance().setCustomModelData(meta, getCustomModelData());
        }

        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Is started boolean.
     *
     * @return the boolean
     */
    public boolean isStarted() {
        return startedTime != 0;
    }

    /**
     * Is timedout boolean.
     *
     * @return the boolean
     */
    public boolean isTimedout() {
        Date date = new Date();
        return startedTime + allowedTime < date.getTime();
    }

    /**
     * Is completed boolean.
     *
     * @return the boolean
     */
    public boolean isCompleted() {
        return missionJson.getCompleted() >= missionJson.getAmount();
    }
}
