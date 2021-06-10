/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.MissionJson;
import world.naturecraft.townymission.components.containers.sql.MissionEntry;
import world.naturecraft.townymission.config.CustomConfigParser;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.List;
import java.util.Random;

/**
 * The type Mission manage gui.
 */
public class MissionManageGui extends TownyMissionGui {

    private final TownyMission instance;
    private final String guiTitle;

    /**
     * Instantiates a new Mission manage gui.
     *
     * @param instance the instance
     */
    public MissionManageGui(TownyMission instance) {
        this.instance = instance;
        guiTitle = "Mission Manage";
        inv = Bukkit.createInventory(null, 36, guiTitle);
    }

    /**
     * Initialize items.
     */
    public void initializeItems(Player player) {
        Town town = TownyUtil.residentOf(player);
        MissionDao missionDao = MissionDao.getInstance();

        // Figure out how many missions the town is missing
        int diff = instance.getConfig().getInt("mission.amount") - missionDao.getNumAdded(town);
        List<MissionJson> missions = CustomConfigParser.parseAll(instance);
        int size = missions.size();
        Random rand = new Random();

        for (int i = 0; i < diff; i++) {
            //TODO: Prevent duplicates
            int index = rand.nextInt(size);
            MissionJson mission = missions.get(index);
            try {
                MissionEntry entry = new MissionEntry(0,
                        mission.getMissionType().name(),
                        Util.currentTime(),
                        0,
                        Util.hrToMs(mission.getHrAllowed()),
                        mission.toJson(),
                        town.getName(),
                        null);
                missionDao.add(entry);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Get and place all town missions
        // TODO: Kinda wasteful, should be combined with adding logic above
        List<MissionEntry> missionList = missionDao.getTownMissions(town);
        int placingIndex = 11;
        for (MissionEntry entry : missionList) {
            if (placingIndex % 9 == 0) {
                placingIndex += 2;
            }

            if (!entry.isStarted()) {
                inv.setItem(placingIndex, entry.getGuiItem());
                placingIndex++;
            }
        }

        // Put in all started missions
        missionList = MissionService.getInstance().getStartedMissions(town);
        placingIndex = 0;
        for (MissionEntry entry : missionList) {
            inv.setItem(0, entry.getGuiItem());
            placingIndex += 9;
        }

        // Place filler glass panes
        placeFiller();
    }

    /**
     * Place filler.
     */
    public void placeFiller() {
        ItemStack itemStack = new ItemStack(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Util.translateColor("&r"));
        itemStack.setItemMeta(meta);

        for (int i = 1; i < 9; i++) {
            inv.setItem(i, itemStack);
        }
        for (int i = 28; i < 36; i++) {
            inv.setItem(i, itemStack);
        }
        for (int i = 1; i < 28; i += 9) {
            inv.setItem(i, itemStack);
        }
    }

    /**
     * Open inventory.
     */
    public void openInventory(Player player) {
        initializeItems(player);
        player.openInventory(inv);
    }

    /**
     * On inventory click.
     *
     * @param e the e
     */
// Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getView().getTitle().equalsIgnoreCase(guiTitle)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        // This means the player is clicking on the fillers
        if ((slot >= 1 && slot <= 8) || (slot >= 28 && slot <= 35) || slot == 10 || slot == 19) {
            inv.setItem(slot, player.getItemOnCursor());
            player.setItemOnCursor(null);
            return;
        }

        // This means that the player is clicking on the unstarted missions
        if ((slot >= 11 && slot <= 17) || (slot >= 20 && slot <= 26)) {
            // Map the slot numbers to mission number
            slot = (slot >= 11 && slot <= 17) ? slot - 10 : slot;
            slot = (slot >= 20 && slot <= 26) ? slot - 12 : slot;
            if (MissionService.getInstance().canStartMission(player)) {
                MissionService.getInstance().startMission(player, slot);
            }

            Town town = TownyUtil.residentOf(player);
            initializeItems(player);
            player.updateInventory();
            player.setItemOnCursor(null);
        }
    }
}
