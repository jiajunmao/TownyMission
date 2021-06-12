/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * The type Towny mission gui.
 */
public abstract class TownyMissionGui implements Listener {

    /**
     * The Inv.
     */
    protected Inventory inv;

    /**
     * On inventory click.
     *
     * @param e the e
     */
// Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        System.out.println("Inventory drag registered");
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }
}
