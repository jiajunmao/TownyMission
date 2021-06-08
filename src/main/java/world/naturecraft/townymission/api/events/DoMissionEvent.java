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

/**
 * The type Do mission event.
 */
public class DoMissionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean isCanceled;
    private final Player player;
    // !!IMPORTANT!! this entry contains the **updated** info
    private final TaskEntry taskEntry;

    /**
     * Instantiates a new Do mission event.
     *
     * @param player the player
     * @param entry  the entry
     */
    public DoMissionEvent(Player player, TaskEntry entry, boolean isAsync) {
        super(isAsync);
        this.player = player;
        this.taskEntry = entry;
        this.isCanceled = false;
    }

    /**
     * Gets handler list.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
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

    /**
     * Is canceled boolean.
     *
     * @return the boolean
     */
    public boolean isCanceled() {
        return isCanceled;
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets task entry.
     *
     * @return the task entry
     */
    public TaskEntry getTaskEntry() {
        return taskEntry;
    }
}
