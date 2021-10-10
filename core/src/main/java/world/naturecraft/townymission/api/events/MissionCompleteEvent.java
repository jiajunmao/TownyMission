package world.naturecraft.townymission.api.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;

import java.util.UUID;

public class MissionCompleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final OfflinePlayer playerCompleted;
    private final long completedTime;
    private final MissionEntry missionEntry;

    public MissionCompleteEvent(OfflinePlayer playerCompleted, long completedTime, MissionEntry missionEntry) {
        this.playerCompleted = playerCompleted;
        this.completedTime = completedTime;
        this.missionEntry = missionEntry;
        this.cancelled = false;
    }

    public OfflinePlayer getPlayerCompleted() {
        return playerCompleted;
    }

    public long getCompletedTime() {
        return completedTime;
    }

    public MissionEntry getMissionEntry() {
        return missionEntry;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
