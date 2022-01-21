/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.gui;

import com.cryptomorin.xseries.XMaterial;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.naturelib.utils.BukkitUtil;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.json.cooldown.CooldownJson;
import world.naturecraft.townymission.components.json.mission.MissionJson;
import world.naturecraft.townymission.config.MissionConfigParser;
import world.naturecraft.townymission.data.dao.CooldownDao;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.MissionService;
import world.naturecraft.townymission.services.core.CooldownService;
import world.naturecraft.townymission.services.core.TimerService;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The type Mission manage gui.
 */
public class MissionManageGui extends TownyMissionGui {

    private final TownyMissionBukkit instance;
    private final String guiTitle;
    private final int row;

    /**
     * Instantiates a new Mission manage gui.
     *
     * @param instance the instance
     */
    public MissionManageGui(TownyMissionBukkit instance) {
        this.instance = instance;
        guiTitle = instance.getGuiLangEntry("mission_manage.title");
        int missionNum = instance.getInstanceConfig().getInt("mission.amount");
        this.row = missionNum / 7;
        if (missionNum > 28) {
            instance.getInstanceLogger().severe("You can only set less than 28 possible missions!!!");
            Bukkit.getPluginManager().disablePlugin(instance);
        }

        inv = Bukkit.createInventory(null, (row + 2) * 9, guiTitle);
    }

    /**
     * Initialize items.
     *
     * @param player the player
     */
    public void initializeItems(Player player) {
        inv.clear();

        Town town = TownyUtil.residentOf(player);

        // If not in recess or not has not started, proceed. Otherwise place fillers;
        if (!TimerService.getInstance().canStartMission()) {
            placeRecessFiller();
            return;
        }

        // Check if there is loaded missions
        List<MissionJson> missions = MissionConfigParser.parseAll(instance, town);
        if (missions.size() == 0) {
            placeFiller();
            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.list.onNoConfiguredMission"));
            instance.getInstanceLogger().severe(ChatService.getInstance().translateColor(instance.getLangEntryNoPrefix("commands.list.onNoConfiguredMission")));
            return;
        }

        // Get all slots where we can add missions to
        List<Integer> missingMission = MissionDao.getInstance().getMissingIndexMissions(town.getUUID());
        missingMission.removeAll(CooldownService.getInstance().getInCooldown(town.getUUID()));

        // Get all missions from town
        List<MissionEntry> townMissions = MissionDao.getInstance().getTownMissions(town.getUUID());

        // If the town is not in cooldown, it can get new missions
        for (Integer index : missingMission) {
            MissionJson mission = missions.get(new Random().nextInt(missions.size()));
            MissionEntry entry = mission.getNewMissionEntry(town.getUUID(), index);
            // Async this in the future and handle concurrency issue with click event
            MissionDao.getInstance().add(entry);

            // **This is to prevent DB connection lag when not using mem-cache
            townMissions.add(entry);
        }

        int startedMissionIdx = 0;
        for (MissionEntry entry : townMissions) {
            int placingIndex = numMissionToSlot(entry.getNumMission());
            if (!entry.isStarted()) {
                inv.setItem(placingIndex, entry.getGuiItem());
            } else if (entry.isStarted()) {
                // Filler on the standby panel
                inv.setItem(placingIndex, startedFiller());

                // Progress on the started panel
                inv.setItem(startedMissionIdx, entry.getGuiItem());
                startedMissionIdx += 9;
            }
        }

        // Cooldown filler
        for (Integer cooldownSlot : CooldownService.getInstance().getInCooldown(town.getUUID())) {
            inv.setItem(numMissionToSlot(cooldownSlot), cooldownFiller(cooldownSlot, town.getUUID()));
        }

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
            meta.setDisplayName(ChatService.getInstance().translateColor(instance.getGuiLangEntry("mission_manage.in_recess_filler.title")));

            List<String> loreList = new ArrayList<>();
            for (String s : instance.getGuiLangEntries("mission_manage.in_recess_filler.lores")) {
                loreList.add(ChatService.getInstance().translateColor("&r" + s));
            }
            meta.setLore(loreList);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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

        // First row
        for (int i = 1; i < 9; i++) {
            inv.setItem(i, itemStack);
        }

        // Last row
        for (int i = (row + 1) * 9 + 2; i < (row + 2) * 9; i++) {
            inv.setItem(i, itemStack);
        }

        // Vertical
        for (int i = 1; i < (row + 2) * 9; i += 9) {
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
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;


        final Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        int row = slot / 9;

        // This means that if its recess time, nothing will happen if you click
        if (!TimerService.getInstance().canStartMission()) {
            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onClickDuringRecess", true));
            return;
        }

        // This means the player is clicking on the fillers
        if ((row == 0 && slot % 9 >= 1) || (row == this.row + 1 && slot % 9 >= 1) || slot % 9 == 1) {
            inv.setItem(slot, player.getItemOnCursor());
            player.setItemOnCursor(null);
            return;
        }

        // This means that the player is clicking on the UNSTARTED missions, starting a mission
        if (slot % 9 >= 2) {
            // Map the slot numbers to mission number
            int missionIdx = slotToNumMission(slot);
            // This is the actual logic of storing into db
            if (MissionService.getInstance().startMission(player.getUniqueId(), missionIdx)) {
                MissionEntry entry = MissionDao.getInstance().getEntries(missionEntry -> missionEntry.getNumMission() == missionIdx
                        && missionEntry.getTownUUID().equals(TownyUtil.residentOf(player).getUUID())).get(0);
                List<MissionEntry> startedMissions = MissionDao.getInstance().getStartedMissions(TownyUtil.residentOf(player).getUUID());

                player.setItemOnCursor(null);
                int startedIdx = 0;
                for (MissionEntry missionEntry : startedMissions) {
                    player.getOpenInventory().getTopInventory().setItem(startedIdx, missionEntry.getGuiItem());
                    startedIdx += 9;
                }
                player.getOpenInventory().getTopInventory().setItem(slot, startedFiller());
                player.updateInventory();

                ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.start.onSuccess")
                        .replace("%type%", entry.getMissionType().name())
                        .replace("%details%", entry.getDisplayLine()));
            } else {
                player.setItemOnCursor(null);
                inv.setItem(slot, clickedItem);
                player.updateInventory();
            }
        }

        // This means that the player is clicking on a STARTED mission
        if (slot % 9 == 0) {
            Town town = TownyUtil.residentOf(player);
            List<MissionEntry> startedList = MissionDao.getInstance().getStartedMissions(town.getUUID());
            int index = slot / 9;
            MissionEntry entry = startedList.get(index);

            // This means abort mission
            if ((entry.isTimedout() && !entry.isCompleted() && entry.isStarted()) ||
                    entry.isStarted() && e.getClick().equals(ClickType.RIGHT)) {

                player.setItemOnCursor(null);
                player.getOpenInventory().getTopInventory().setItem(slot, null);
                player.getOpenInventory().getTopInventory().setItem(numMissionToSlot(entry.getNumMission()), cooldownFiller(entry.getNumMission(), entry.getTownUUID(), true));
                player.updateInventory();

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

                player.setItemOnCursor(null);
                player.getOpenInventory().getTopInventory().setItem(slot, null);
                player.getOpenInventory().getTopInventory().setItem(numMissionToSlot(entry.getNumMission()), cooldownFiller(entry.getNumMission(), entry.getTownUUID()));
                player.updateInventory();

                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        MissionService.getInstance().completeMission(player, entry);
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.claim.onSuccess", true).replace("%points%", String.valueOf(entry.getMissionJson().getReward())));
                    }
                };
                runnable.runTaskAsynchronously(instance);
            }
        }
    }

    private int numMissionToSlot(int numMission) {
        int row = numMission / 7 + 1;
        int slot = 9 + row * 2 + (row - 1) * 7 + numMission % 7;
        return slot;
    }

    private int slotToNumMission(int slot) {
        int row = slot / 9;
        if (row == 0 || row == instance.getInstanceConfig().getInt("mission.amount") / 7 + 1)
            return -1;

        int idxInRow = slot % 9;
        return (row - 1) * 7 + idxInRow - 2;
    }

    private ItemStack startedFiller() {
        ItemStack itemStack = new ItemStack(XMaterial.NETHER_STAR.parseMaterial());
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(BukkitUtil.translateColor(instance.getGuiLangEntry("mission_manage.in_progress_filler.title")));
        List<String> loreList = new ArrayList<>();
        for (String s : instance.getGuiLangEntries("mission_manage.in_progress_filler.lores")) {
            loreList.add(ChatService.getInstance().translateColor("&r" + s));
        }
        im.setLore(loreList);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(im);
        return itemStack;
    }

    private ItemStack cooldownFiller(int numMission, UUID townUUID) {
        return cooldownFiller(numMission, townUUID, false);
    }

    private ItemStack cooldownFiller(int numMission, UUID townUUID, boolean staticRemainingTime) {
        ItemStack itemStack = new ItemStack(XMaterial.FIREWORK_STAR.parseMaterial());
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(BukkitUtil.translateColor(instance.getGuiLangEntry("mission_manage.cooldown_filler.title")));
        List<String> loreList = new ArrayList<>();
        for (String s : instance.getGuiLangEntries("mission_manage.cooldown_filler.lores")) {
            long remainingTime;
            if (staticRemainingTime) {
                remainingTime = TimeUnit.MILLISECONDS.convert(instance.getInstanceConfig().getInt("mission.cooldown"), TimeUnit.MINUTES);
            } else {
                CooldownJson cooldownJson = CooldownDao.getInstance().get(townUUID).getCooldownJsonList().getCooldownMap().get(numMission);
                remainingTime = cooldownJson.getStartedTime() + cooldownJson.getCooldown() - new Date().getTime();
            }
            loreList.add(ChatService.getInstance().translateColor("&r" + s.replace("%time%", Util.formatMilliseconds(remainingTime))));
        }
        im.setLore(loreList);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(im);
        return itemStack;
    }
}
