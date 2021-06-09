/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.json.MissionJson;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.config.CustomConfigParser;
import world.naturecraft.townymission.dao.TaskDao;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.List;
import java.util.Random;

public class MissionManageGui implements Listener {

    private final Inventory inv;
    private TownyMission instance;
    private Player player;

    public MissionManageGui(TownyMission instance, Player player) {
        this.instance = instance;
        this.player = player;
        inv = Bukkit.createInventory(null, 36, "Mission Manage");
        initializeItems();
    }

    public void initializeItems() {
        Town town = TownyUtil.residentOf(player);
        TaskDao taskDao = (TaskDao)instance.getDao(DbType.TASK);
        int diff = instance.getConfig().getInt("mission.amount") - taskDao.getNumAdded(town);
        List<MissionJson> missions = CustomConfigParser.parseAll(instance);
        int size = missions.size();
        Random rand = new Random();

        for (int i = 0; i < diff; i++) {
            //TODO: Prevent duplicates
            int index = rand.nextInt(size);
            MissionJson mission = missions.get(index);
            try {
                TaskEntry entry = new TaskEntry(0,
                        mission.getMissionType().name(),
                        Util.currentTime(),
                        0,
                        Util.hrToMs(mission.getHrAllowed()),
                        mission.toJson(),
                        town.getName(),
                        null);
                taskDao.add(entry);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        List<TaskEntry> taskList = taskDao.getTownTasks(town);
        int placingIndex = 11;
        for (int i = 0; i < taskList.size(); i++) {
            if (placingIndex % 9 == 0) {
                placingIndex += 2;
            }

            inv.setItem(placingIndex, taskList.get(i).getGuiItem());
            placingIndex++;
        }
        placeFiller();
    }

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

    public void openInventory() {
        player.openInventory(inv);
    }
}
