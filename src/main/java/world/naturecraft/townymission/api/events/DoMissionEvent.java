/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.containers.sql.TaskEntry;
import world.naturecraft.townymission.components.enums.MissionType;

public class DoMissionEvent extends Event implements Cancellable {

    private boolean isCanceled;
    private Player player;
    // !!IMPORTANT!! this entry contains the **updated** info
    private TaskEntry taskEntry;
    private static final HandlerList handlers = new HandlerList();

    public DoMissionEvent(Player player, TaskEntry entry) {
        this.player = player;
        this.taskEntry = entry;
        this.isCanceled = false;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return isCanceled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.isCanceled = cancel;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public Player getPlayer() {
        return player;
    }

    public TaskEntry getTaskEntry() {
        return taskEntry;
    }
}
