/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.gui;

import com.cryptomorin.xseries.XMaterial;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.json.mission.MissionJson;
import world.naturecraft.townymission.config.mission.MissionConfigParser;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.CooldownService;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.services.TimerService;
import world.naturecraft.townymission.utils.TownyUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The type Mission manage gui.
 */
public class MissionManageGui extends TownyMissionGui {

    private final TownyMissionBukkit instance;
    private final String guiTitle;

    /**
     * Instantiates a new Mission manage gui.
     *
     * @param instance the instance
     */
    public MissionManageGui(TownyMissionBukkit instance) {
        this.instance = instance;
        guiTitle = "Mission Manage";
        inv = Bukkit.createInventory(null, 36, guiTitle);
    }

    /**
     * Initialize items.
     *
     * @param player the player
     */
    public void initializeItems(Player player) {
        inv.clear();
        Town town = TownyUtil.residentOf(player);

        // Figure out how many missions the town is missing
        List<MissionEntry> townMissions = MissionDao.getInstance().getTownMissions(town.getUUID());
        List<MissionEntry> newlyAddedMissions = new ArrayList<>();
        int diff = instance.getConfig().getInt("mission.amount") - townMissions.size();

        List<MissionJson> missions = MissionConfigParser.parseAll(instance);
        int size = missions.size();

        Random rand = new Random();

        // If not in recess or not has not started, proceed. Otherwise place fillers;
        if (!TimerService.getInstance().canStart()) {
            placeFiller();
            placeRecessFiller();
            return;
        }

        if (size == 0) {
            placeFiller();
            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.list.onNoConfiguredMission"));
            instance.getInstanceLogger().severe(ChatService.getInstance().translateColor(instance.getLangEntryNoPrefix("commands.list.onNoConfiguredMission")));
            return;
        }

        int numAddable = CooldownService.getInstance().getNumAddable(town.getUUID());
        diff = Math.min(numAddable, diff);

        // If the town is not in cooldown, it can get new missions
        for (int i = 0; i < diff; i++) {
            int index = rand.nextInt(size);
            MissionJson mission = missions.get(index);
            MissionEntry entry = new MissionEntry(
                    UUID.randomUUID(),
                    mission.getMissionType().name(),
                    new Date().getTime(),
                    0,
                    TimeUnit.MILLISECONDS.convert(mission.getHrAllowed(), TimeUnit.HOURS),
                    mission.toJson(),
                    town.getUUID(),
                    null);
            newlyAddedMissions.add(entry);
            // Async this in the future and handle concurrency issue with click event
            MissionDao.getInstance().add(entry);
        }


        // Place down all missions
        townMissions.addAll(newlyAddedMissions);
        int placingIndex = 11;
        for (MissionEntry entry : townMissions) {
            if (placingIndex % 9 == 0) {
                placingIndex += 2;
            }

            if (!entry.isStarted()) {
                inv.setItem(placingIndex, entry.getGuiItem());
                placingIndex++;
            }
        }

        // Put in all started missions
        townMissions = MissionDao.getInstance().getStartedMissions(town.getUUID());
        placingIndex = 0;
        for (MissionEntry entry : townMissions) {
            inv.setItem(0, entry.getGuiItem());
            placingIndex += 9;
        }

        // Place filler glass panes
        placeFiller();
    }

    /**
     * Place fillers that show we are currently in recess
     */
    public void placeRecessFiller() {
        int placingIndex = 11;
        for (int i = 0; i < instance.getConfig().getInt("mission.amount"); i++) {
            if (placingIndex % 9 == 0) {
                placingIndex += 2;
            }

            ItemStack itemStack = new ItemStack(Material.FIREWORK_STAR, 1);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(ChatService.getInstance().translateColor("&cIn Recess"));

            List<String> lore = new ArrayList<>();
            lore.add(ChatService.getInstance().translateColor("&r&7We are currently in recess"));
            lore.add(ChatService.getInstance().translateColor("&r&7Please wait for the sprint to start"));

            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            inv.setItem(placingIndex, itemStack);
            placingIndex++;
        }
    }

    /**
     * Place filler.
     */
    public void placeFiller() {
        ItemStack itemStack = new ItemStack(new ItemStack(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial(), 1));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatService.getInstance().translateColor("&r"));
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
     *
     * @param player the player
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
        // This means the opened inv is not this inv
        if (!e.getView().getTitle().equalsIgnoreCase(guiTitle)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        final Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        // This means that if the its recess time, nothing will happen if you click
        if (!TimerService.getInstance().canStart()) {
            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onClickDuringRecess"));
            return;
        }

        // This means the player is clicking on the fillers
        if ((slot >= 1 && slot <= 8) || (slot >= 28 && slot <= 35) || slot == 10 || slot == 19) {
            inv.setItem(slot, player.getItemOnCursor());
            player.setItemOnCursor(null);
            return;
        }

        // This means that the player is clicking on the UNSTARTED missions, starting a mission
        if ((slot >= 11 && slot <= 17) || (slot >= 20 && slot <= 26)) {
            // Map the slot numbers to mission number
            int missionIdx;

            if (slot >= 11 && slot <= 17) {
                missionIdx = slot - 10;
            } else {
                missionIdx = slot - 12;
            }

            // This is the actual logic of storing into db
            if (MissionService.getInstance().startMission(player.getUniqueId(), missionIdx)) {
                int firstEmpty = getFirstEmptyStartRegionSlot();
                player.setItemOnCursor(null);
                initializeItems(player);
                inv.setItem(missionIdx, null);
                inv.setItem(firstEmpty, clickedItem);
                player.openInventory(inv);
                Town town = TownyUtil.residentOf(player);

                final int missionIdxFinal = missionIdx;
                BukkitRunnable r = new BukkitRunnable() {
                    @Override
                    public void run() {
                        MissionEntry entry = MissionDao.getInstance().getIndexedMission(town.getUUID(), missionIdxFinal);

                        try {
                            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.start.onSuccess")
                                    .replace("%type%", entry.getMissionType().name())
                                    .replace("%details%", entry.getDisplayLine()));
                        } catch (JsonProcessingException exception) {
                            exception.printStackTrace();
                        }
                    }
                };

                r.runTaskAsynchronously(instance);
            } else {
                player.setItemOnCursor(null);
                initializeItems(player);
                inv.setItem(slot, clickedItem);
                player.openInventory(inv);
            }
        }

        // This means that the player is clicking on a STARTED mission
        if (slot == 0 || slot == 9 || slot == 18 || slot == 27) {
            Town town = TownyUtil.residentOf(player);
            List<MissionEntry> startedList = MissionDao.getInstance().getStartedMissions(town.getUUID());
            int index = slot / 9;
            MissionEntry entry = startedList.get(index);

            // This means abort mission
            if (entry.isTimedout() && !entry.isCompleted() && entry.isStarted()) {
                player.setItemOnCursor(null);
                initializeItems(player);
                inv.setItem(slot, null);
                player.openInventory(inv);
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        MissionService.getInstance().abortMission(player.getUniqueId(), entry, false);
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.abort.onSuccess"));
                    }
                };
                runnable.runTaskAsynchronously(instance);
            }

            // This means complete the mission
            else if (entry.isCompleted() && entry.isStarted()) {
                player.setItemOnCursor(null);
                initializeItems(player);
                inv.setItem(slot, null);
                player.openInventory(inv);
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        MissionService.getInstance().completeMission(entry);
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.claim.onSuccess").replace("%points%", String.valueOf(entry.getMissionJson().getReward())));
                    }
                };
                runnable.runTaskAsynchronously(instance);
            }

            // If on going, and right click, that means abort
            else if (entry.isStarted() && e.getClick().equals(ClickType.RIGHT)) {
                player.setItemOnCursor(null);
                initializeItems(player);
                inv.setItem(slot, null);
                player.openInventory(inv);
                MissionService.getInstance().abortMission(player.getUniqueId(), entry, false);
                ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.abort.onSuccess"));
            }
        }
    }

    /**
     * Gets first empty start region slot.
     *
     * @return the first empty start region slot
     */
    public Integer getFirstEmptyStartRegionSlot() {
        for (int i = 0; i < 28; i += 9) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                return i;
            }
        }

        return null;
    }
}
